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
            case "infoOutput": $("#informationLogs")[0].innerHTML += "<div>" + ansi2html_string(message.payloadString.replaceAll('[38;5;68m', '<span class="ansi_fg_68m">\t').replace(/\033\[0m/g, '</span>')).replace(/\033/g, '').replace('/*\t- INFORMATION -', '/*&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-').replace('\t- INFORMATION - */','&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-&nbsp;*/').replaceAll('\n', '<br />').replaceAll("* ", "&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;&nbsp;").replaceAll("--------------------------------------------------------------------------------", "-------------------------------------------------------------") + "</div>"; break;

            case "jiraActivityStreamOutput": $("#jiraActivityStream div")[0].innerHTML += ansi2html_string(message.payloadString).replace(/  +/mg, function (match) {
                return match.replace(/ /g, "&nbsp;");
            }).replaceAll('&nbsp;|&nbsp;', '|').replaceAll('/', '').replaceAll('\n', '<br />').replaceAll(/^─|( ─)/g, '&nbsp;&nbsp;─').concat('<br/>'); break;

            case "errorOutput": $("#errorLogs")[0].innerHTML += "<div>" + ansi2html_string(message.payloadString.replaceAll('[38;5;196m', '<span class="ansi_fg_red">\t').replace(/\033\[0m/g, '</span>')).replace(/\033/g, '').replace('/*\t- !ERROR! -', '/*&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-').replace('\t- !ERROR! - */','&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-&nbsp;*/').replaceAll('\n', '<br />').replaceAll("!-- ", "&nbsp;&nbsp;&nbsp;&nbsp;!--&nbsp;&nbsp;").replaceAll("--------------------------------------------------------------------------------", "-------------------------------------------------------------") + "</div>"; break;
        }
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

    if (location.protocol === "https:") {
        options.useSSL = true;
    }

    client.connect(options);

    $("#jiraActivityStreamBtn")[0].addEventListener('click', () => $("#jiraActivityStream")[0].show());

    $("#minimalEpicsCount")[0].addEventListener('sl-input', () => {
        if (parseInt($("#minimalEpicsCount")[0].value) < parseInt($("#maximalEpicsCount")[0].value)) {
            $("#minimalEpicsCount")[0].setCustomValidity('');
            $("#maximalEpicsCount")[0].setCustomValidity('');
        } else if (parseInt($("#minimalEpicsCount")[0].value) < 0 || parseInt($("#minimalEpicsCount")[0].value) > 999){
            $("#minimalEpicsCount")[0].setCustomValidity("Invalid value - must positive integer less than 1000");
        } else {
            $("#minimalEpicsCount")[0].setCustomValidity("Invalid value - must be lower than max value");
        }
    });

    $("#maximalEpicsCount")[0].addEventListener('sl-input', () => {
        if (parseInt($("#maximalEpicsCount")[0].value) > parseInt($("#minimalEpicsCount")[0].value)) {
            $("#minimalEpicsCount")[0].setCustomValidity('');
            $("#maximalEpicsCount")[0].setCustomValidity('');
        } else if (parseInt($("#maximalEpicsCount")[0].value) < 0 || parseInt($("#maximalEpicsCount")[0].value) > 999){
            $("#maximalEpicsCount")[0].setCustomValidity("Invalid value - must positive integer less than 1000");
        } else {
            $("#maximalEpicsCount")[0].setCustomValidity("Invalid value - must be greater than min value");
        }
    });

    $("#create-epic-form #submit-button")[0].addEventListener("click", async () => {
        if($("#minimalEpicsCount")[0].hasAttribute("data-valid") && $("#maximalEpicsCount")[0].hasAttribute("data-valid"))
            $.ajax({
                type: "OPTIONS",
                url: "/api/applicationFlowRandomized?".concat("min=".concat($("#minimalEpicsCount")[0].value).concat("&max=").concat($("#maximalEpicsCount")[0].value)),
                success: () => {
                    $("#create-epic-form")[0].reset();
                }
            });
        else
            notify("Cannot start application flow - Data is invalid", "error", "exclamation-octagon");
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