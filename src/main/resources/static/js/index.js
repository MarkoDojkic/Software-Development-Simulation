import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/alert/alert.js';
import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/popup/popup.js';
import {ansi2html_string} from './ansi2html.js';

$(window).on("load", async () => {
    const websocketBroker = location.hostname;
    const websocketPort = 15675;

    const client = new Paho.MQTT.Client(websocketBroker, websocketPort, "/ws", "fe-client_" + (Math.random() * 100).toString(10));

    client.onConnectionLost = responseObject => {
        notify("MQTT connection failure to" + websocketBroker + ":" + websocketPort + "<br />" + responseObject.errorMessage, "warning", "exclamation-triangle");
    };

    client.onMessageArrived = message => {
        const jiraActivityStreamDiv  = $("#jiraActivityStream div")[0];
        switch(message.destinationName){
            case "infoOutput": $("#informationLogs")[0].innerHTML += "<div>" + ansi2html_string(message.payloadString.replaceAll('[38;5;68m', '<span class="ansi_fg_68m">\t').replace(/\033\[0m/g, '</span>')).replace(/\033/g, '').replace('/*\t- INFORMATION -', '/*&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-').replace('\t- INFORMATION - */','&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-&nbsp;*/').replaceAll('\n', '<br />').replaceAll("* ", "&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;&nbsp;").replaceAll("--------------------------------------------------------------------------------", "-------------------------------------------------------------") + "</div>"; break;

            case "jiraActivityStreamOutput": jiraActivityStreamDiv.innerHTML = ansi2html_string(message.payloadString).replace(/  +/mg, function (match) {
                return match.replace(/ /g, "&nbsp;");
            }).replaceAll('&nbsp;|&nbsp;', '|').replaceAll('/', '').replaceAll('\n', '<br />').replaceAll(/^─|( ─)/g, '&nbsp;&nbsp;─').concat('<br/>') + jiraActivityStreamDiv.innerHTML; break;

            case "errorOutput": $("#errorLogs")[0].innerHTML += "<div>" + ansi2html_string(message.payloadString.replaceAll('[38;5;196m', '<span class="ansi_fg_red">\t').replace(/\033\[0m/g, '</span>')).replace(/\033/g, '').replace('/*\t- !ERROR! -', '/*&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-').replace('\t- !ERROR! - */','&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-&nbsp;*/').replaceAll('\n', '<br />').replaceAll("!-- ", "&nbsp;&nbsp;&nbsp;&nbsp;!--&nbsp;&nbsp;").replaceAll("--------------------------------------------------------------------------------", "-------------------------------------------------------------") + "</div>"; break;
        }
    }

    const options = {
        timeout: 10,
        keepAliveInterval: 3600,
        onSuccess: function () {
            notify("MQTT connected successfully to " + websocketBroker + ":" + websocketPort, "success", "check2-circle");
            client.subscribe("infoOutput", {qos: 1});
            client.subscribe("jiraActivityStreamOutput", {qos: 1});
            client.subscribe("errorOutput", {qos: 1});

            notify(`MQTT subscribed to:
                <ul>
                    <li>infoOutput</li>
                    <li>jiraActivityStreamOutput</li>
                    <li>errorOutput</li>
                </ul>
            `);
        },
        onFailure: function (message) {
            notify("MQQT connection failure to" + websocketBroker + ":" + websocketPort + "<br />" + message.errorMessage, "error", "exclamation-octagon");
        }
    };

    if (location.protocol === "https:") {
        options.useSSL = true;
    }

    client.connect(options);

    const minimalEpicsCount = $("#minimalEpicsCount")[0];
    const maximalEpicsCount = $("#maximalEpicsCount")[0];

    $("#jiraActivityStreamBtn")[0].addEventListener('click', () => $("#jiraActivityStream")[0].show());

    minimalEpicsCount.addEventListener('sl-input', () => {
        if (parseInt(minimalEpicsCount.value) < parseInt(maximalEpicsCount.value)) {
            minimalEpicsCount.setCustomValidity('');
            maximalEpicsCount.setCustomValidity('');
        } else if (parseInt(minimalEpicsCount.value) < 0 || parseInt(minimalEpicsCount.value) > 999){
            minimalEpicsCount.setCustomValidity("Invalid value - must positive integer less than 1000");
        } else {
            minimalEpicsCount.setCustomValidity("Invalid value - must be lower than max value");
        }
    });

    maximalEpicsCount.addEventListener('sl-input', () => {
        if (parseInt(maximalEpicsCount.value) > parseInt(minimalEpicsCount.value)) {
            minimalEpicsCount.setCustomValidity('');
            maximalEpicsCount.setCustomValidity('');
        } else if (parseInt(maximalEpicsCount.value) < 0 || parseInt(maximalEpicsCount.value) > 999){
            maximalEpicsCount.setCustomValidity("Invalid value - must positive integer less than 1000");
        } else {
            maximalEpicsCount.setCustomValidity("Invalid value - must be greater than min value");
        }
    });

    $("#create-epic-form #submit-button")[0].addEventListener("click", async () => {
        if(minimalEpicsCount.hasAttribute("data-valid") && maximalEpicsCount.hasAttribute("data-valid"))
            $.ajax({
                type: "OPTIONS",
                url: "/api/applicationFlowRandomized?".concat("min=".concat(minimalEpicsCount.value).concat("&max=").concat(maximalEpicsCount.value)),
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