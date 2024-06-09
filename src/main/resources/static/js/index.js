import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/alert/alert.js';
import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/popup/popup.js';
import {ansi2html_string} from './ansi2html.js';

$(window).on("load", async () => {
    var wsbroker = location.hostname;
    var wsport = 15675;

    var client = new Paho.MQTT.Client(wsbroker, wsport, "/ws", "fe-client_" +  parseInt(Math.random() * 100, 10));

    client.onConnectionLost = responseObject => {
        notify("MQQT connection failure to" + wsbroker + ":" + wsport + "<br />" + responseObject.errorMessage, "warning", "exclamation-triangle");
    };

    client.onMessageArrived = message => {
        switch(message.destinationName){
            case "infoOutput": $("#informationLogs")[0].innerHTML += "<div>" + ansi2html_string(message.payloadString.replaceAll('[38;5;68m', '<span class="ansi_fg_68m">\t').replace(/\033\[0m/g, '</span>')).replace(/\033/g, '').replace('/*\t- INFORMATION -', '/*&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-').replace('\t- INFORMATION - */','&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-&nbsp;*/').replaceAll('\n', '<br />') + "</div>"; break;

            case "jiraActivityStreamOutput": $("#jiraActivityStream div")[0].innerHTML += ansi2html_string(message.payloadString).replace(/  +/mg, function (match) {
                return match.replace(/ /g, "&nbsp;");
            }).replaceAll('&nbsp;|&nbsp;', '|').replaceAll('/', '').replaceAll('\n', '<br />').replaceAll(/^─|( ─)/g, '&nbsp;&nbsp;─').concat('<br/>'); break;

            case "errorOutput": $("#errorLogs")[0].innerHTML += "<div>" + ansi2html_string(message.payloadString.replaceAll('[38;5;196m', '<span class="ansi_fg_red">\t').replace(/\033\[0m/g, '</span>')).replace(/\033/g, '').replace('/*\t- !ERROR! -', '/*&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-').replace('\t- !ERROR! - */','&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-&nbsp;*/').replaceAll('\n', '<br />') + "</div>"; break;
        };
    }

    var options = {
        timeout: 10,
        keepAliveInterval: 3600,
        onSuccess: function() {
            notify("MQQT connected successfully to " + wsbroker + ":" + wsport, "success", "check2-circle");
            client.subscribe("infoOutput", { qos: 1 });
            client.subscribe("jiraActivityStreamOutput", { qos: 1 });
            client.subscribe("errorOutput", { qos: 1 });

            notify(`MQQT subscribed to:
                <ul>
                    <li>infoOutput</li>
                    <li>jiraActivityStreamOutput</li>
                    <li>errorOutput</li>
                </ul>
            `);
        },
        onFailure: function(message) {
            notify("MQQT connection failure to" + wsbroker + ":" + wsport + "<br />" + message.errorMessage, "error", "exclamation-octagon");
        }
    };

    if (location.protocol == "https:") {
        options.useSSL = true;
    }

    client.connect(options);

    $("#jiraActivityStreamBtn")[0].addEventListener('click', () => $("#jiraActivityStream")[0].show());

    $("#create-epic-form #submit-button")[0].addEventListener("click", async e => {
        $.ajax({
            type: "OPTIONS",
            url: "/api/applicationFlowRandomized?".concat("min=".concat($("#minimalEpicsCount")[0].value).concat("&max=").concat($("#maximalEpicsCount")[0].value)),
            success: response => {
                $("#create-epic-form")[0].reset();
            }
        });
    });
});

function notify(message, variant = 'primary', icon = 'info-circle', duration = 1500) {
    alert = Object.assign(document.createElement('sl-alert'), {
      variant,
      closable: false,
      duration: duration,
      innerHTML: `
        <sl-icon name="${icon}" slot="icon"></sl-icon>
        ${message}
      `
    });

    document.body.append(alert);

    alert.toast();
}