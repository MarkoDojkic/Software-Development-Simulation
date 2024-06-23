import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/alert/alert.js';
import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/popup/popup.js';
import {ansi2html_string} from './ansi2html.js';

$(window).on("load", async () => {
    const websocketBroker = location.hostname;
    const websocketPort = 15675;

    const client = new Paho.MQTT.Client(websocketBroker, websocketPort, "/ws", "fe-client_".concat(generateUUID()));

    client.onConnectionLost = responseObject => {
        notify("MQTT connection failure to" + websocketBroker + ":" + websocketPort + "<br />" + responseObject.errorMessage, "warning", "exclamation-triangle");
    };

    client.onMessageArrived = message => appendDataToMQTTTopicDivs(message.destinationName, message.payloadString);

    const options = {
        keepAliveInterval: 300,
        cleanSession: false,
        mqttVersion: 4, // Identification for version 3.1.1
        onSuccess: function () {
            notify("MQTT connected successfully to " + websocketBroker + ":" + websocketPort, "success", "check2-circle");
            client.subscribe("information-printout-topic", {qos: 2});
            client.subscribe("java-activity-stream-printout-topic", {qos: 2});
            client.subscribe("error-printout-topic", {qos: 2});

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

    $.ajax({
        type: "GET",
        url: "/api/logs?filename=informationChannel",
        success: response => {
            response.split("%$").forEach(instance => {
                appendDataToMQTTTopicDivs("information-printout-topic", instance);
            });
        }
    });

    $.ajax({
        type: "GET",
        url: "/api/logs?filename=jiraActivityStreamChannel",
        success: response => {
            response.split("%$").forEach(instance => {
                appendDataToMQTTTopicDivs("java-activity-stream-printout-topic", instance);
            });
        }
    });

    $.ajax({
        type: "GET",
        url: "/api/logs?filename=errorChannel",
        success: response => {
            response.split("%$").forEach(instance => {
                appendDataToMQTTTopicDivs("error-printout-topic", instance);
            });
        }
    });

    $('#sl-rating-developer')[0].getSymbol = (() => '<sl-icon name="code-slash"></sl-icon>');
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

function generateUUID() {
    const crypto = window.crypto || window.msCrypto;
    if (!crypto) {
        console.error("Crypto API not available");
        return (Math.random() * 1000).toFixed(0);
    }
    const array = new Uint32Array(4);
    crypto.getRandomValues(array);
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = (array[0] + Math.random() * 16) % 16 | 0;
        array[0] = Math.floor(array[0] / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}

function appendDataToMQTTTopicDivs(topicName, data) {
    if(data.length === 0 || data === "\n") return;
    switch (topicName) {
        case "information-printout-topic":
        {
            const sanitizedData = sanitizeInformationData(data);
            $("#informationLogs").append(`<div>${sanitizedData.replace(/^<br\s*\/?>/, '')}</div>`);
            break;
        }
        case "java-activity-stream-printout-topic":
        {
            const sanitizedData = sanitizeJavaActivityStreamData(data);
            $("#jiraActivityStream div").prepend(sanitizedData.replace(/^<br\s*\/?>/, ''));
            break;
        }
        case "error-printout-topic":
        {
            const sanitizedData = sanitizeErrorData(data);
            $("#errorLogs").append(`<div>${sanitizedData.replace(/^<br\s*\/?>/, '')}</div>`);
            break;
        }
    }
}

function sanitizeInformationData(data) {
    return ansi2html_string(data.replaceAll('[38;5;68m', '<span class="ansi_fg_68m">\t')
        .replace(/\033\[0m/g, '</span>')
        .replace(/\033/g, '')
        .replace('/*\t- INFORMATION -', '/*&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-')
        .replace('\t- INFORMATION - */', '&nbsp;&nbsp;-&nbsp;INFORMATION&nbsp;-&nbsp;*/')
        .replaceAll('\n', '<br />')
        .replaceAll("* ", "&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;&nbsp;")
        .replaceAll("--------------------------------------------------------------------------------", "-------------------------------------------------------------"));
}

function sanitizeJavaActivityStreamData(data) {
    return ansi2html_string(data.replace(/  +/mg, match => match.replace(/ /g, "&nbsp;"))
        .replaceAll('&nbsp;|&nbsp;', '|')
        .replaceAll('/', '')
        .replaceAll('\n', '<br />')
        .replaceAll(/^─|( ─)/g, '&nbsp;&nbsp;─')
        .concat('<br/>'));
}

function sanitizeErrorData(data) {
    return ansi2html_string(data.replaceAll('[38;5;196m', '<span class="ansi_fg_red">\t')
        .replace(/\033\[0m/g, '</span>')
        .replace(/\033/g, '')
        .replace('/*\t- !ERROR! -', '/*&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-')
        .replace('\t- !ERROR! - */', '&nbsp;&nbsp;-&nbsp;!ERROR!&nbsp;-&nbsp;*/')
        .replaceAll('\n', '<br />')
        .replaceAll("!-- ", "&nbsp;&nbsp;&nbsp;&nbsp;!--&nbsp;&nbsp;")
        .replaceAll("--------------------------------------------------------------------------------", "-------------------------------------------------------------"));
}
