import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/alert/alert.js';
import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/popup/popup.js';
import {ansi2html_string} from './ansi2html.js';

$(window).on("load", async () => {
    const websocketBroker = location.hostname;
    const websocketPort = 15675;

    const client = new Paho.MQTT.Client(websocketBroker, websocketPort, "/ws", "fe-client_".concat(generateUUID()));

    client.onConnectionLost = responseObject => {
        notifyWarning("MQTT connection lost (" + websocketBroker + ":" + websocketPort + ")<br />" + responseObject.errorMessage);
    };

    client.onMessageArrived = message => appendDataToMQTTTopicDivs(message.destinationName, message.payloadString);

    const options = {
        keepAliveInterval: 300,
        cleanSession: false,
        mqttVersion: 4, // Identification for version 3.1.1
        onSuccess: function () {
            notifySuccess("MQTT connected successfully to " + websocketBroker + ":" + websocketPort);
            client.subscribe("information-printout-topic", {qos: 2});
            client.subscribe("java-activity-stream-printout-topic", {qos: 2});
            client.subscribe("error-printout-topic", {qos: 2});

            notifyInfo(`MQTT subscribed to:
                <ul>
                    <li>infoOutput</li>
                    <li>jiraActivityStreamOutput</li>
                    <li>errorOutput</li>
                </ul>
            `);
        },
        onFailure: function (message) {
            notifyError("MQQT connection failure (" + websocketBroker + ":" + websocketPort + ")<br />" + message.errorMessage);
        }
    };

    if (location.protocol === "https:") {
        options.useSSL = true;
    }

    client.connect(options);

    const minimalEpicsCount = $("#minimalEpicsCount");
    const maximalEpicsCount = $("#maximalEpicsCount");

    $("#jiraActivityStreamSlButton").on('click', () => $("#jiraActivityStream")[0].show());

    $("#manageCustomEpicsSlButton").on('click', () => $("#customEpics")[0].show());
    $("#manageCustomUserStoriesSlButton").on('click', () => $("#customUserStories")[0].show());
    $("#manageCustomTechnicalTasksSlButton").on('click', () => $("#customTechnicalTasks")[0].show());

    minimalEpicsCount.on('sl-input', () => {
        if (parseInt(minimalEpicsCount.val()) < parseInt(maximalEpicsCount.val())) {
            minimalEpicsCount.get(0).setCustomValidity('');
            maximalEpicsCount.get(0).setCustomValidity('');
        } else if (parseInt(minimalEpicsCount.val()) < 0 || parseInt(minimalEpicsCount.val()) > 999) {
            minimalEpicsCount.get(0).setCustomValidity("Invalid value - must positive integer less than 1000");
        } else {
            minimalEpicsCount.get(0).setCustomValidity("Invalid value - must be lower than max value");
        }
    });

    maximalEpicsCount.on('sl-input', () => {
        if (parseInt(maximalEpicsCount.val()) > parseInt(minimalEpicsCount.val())) {
            minimalEpicsCount.get(0).setCustomValidity('');
            maximalEpicsCount.get(0).setCustomValidity('');
        } else if (parseInt(maximalEpicsCount.val()) < 0 || parseInt(maximalEpicsCount.val()) > 999) {
            maximalEpicsCount.get(0).setCustomValidity("Invalid value - must positive integer less than 1000");
        } else {
            maximalEpicsCount.get(0).setCustomValidity("Invalid value - must be greater than min value");
        }
    });

    $("#simulatePredefinedSlButton").on("click", async () => {
        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];

        if (customData.length > 0)
            $.ajax({
                type: "POST",
                url: "/api/applicationFlowPredefined",
                contentType: 'application/json',
                data: JSON.stringify(customData),
                success: response => notifySuccess(response),
                error: response => notifyError(response.responseText)
            });
        else
            notifyError("Cannot start application flow - There aren`t any predefined data");
    });

    $("#generateRandomDataFlowForm #simulateRandomizedSlButton").on("click", async () => {
        if (minimalEpicsCount.attr("data-valid") !== undefined && maximalEpicsCount.attr("data-valid") !== undefined)
            $.ajax({
                type: "POST",
                url: "/api/applicationFlowRandomized?".concat("save=".concat($("#saveRandomizedDataToFile")[0].checked.toString().concat("&min=".concat(minimalEpicsCount.val().concat("&max=".concat(maximalEpicsCount.val())))))),
                success: () => $("#generateRandomDataFlowForm").trigger('reset'),
            });
        else
            notifyError("Cannot start application flow - Data is invalid");
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

    const epicReporter = $("sl-select[name='epicReporter']");
    const epicAssignee = $("sl-select[name='epicAssignee']");

    $("sl-select[name='selectedEpicDevelopmentTeam']").on("sl-change", (event) => {
        epicReporter.prop("disabled", false);
        epicAssignee.prop("disabled", false);
        epicReporter.html($("#developmentTeamsListOfDevelopers #".concat(event.target.value)).html());
        epicAssignee.html($("#developmentTeamsListOfDevelopers #".concat(event.target.value)).html());
    });

    const userStoryReporter = $("sl-select[name='userStoryReporter']");
    const userStoryAssignee = $("sl-select[name='userStoryAssignee']");

    $("sl-select[name='selectedEpic']").on("sl-change", (event) => {
        userStoryReporter.prop("disabled", false);
        userStoryAssignee.prop("disabled", false);
        userStoryReporter.html($("#developmentTeamsListOfDevelopers #".concat(event.target.value.split("$")[1])).html());
        userStoryAssignee.html($("#developmentTeamsListOfDevelopers #".concat(event.target.value.split("$")[1])).html());
    });

    const technicalTaskReporter = $("sl-select[name='technicalTaskReporter']");
    const technicalTaskAssignee = $("sl-select[name='technicalTaskAssignee']");

    $("sl-select[name='selectedUserStory']").on("sl-change", (event) => {
        technicalTaskReporter.prop("disabled", false);
        technicalTaskAssignee.prop("disabled", false);
        technicalTaskReporter.html($("#developmentTeamsListOfDevelopers #".concat(event.target.value.split("$")[1])).html());
        technicalTaskAssignee.html($("#developmentTeamsListOfDevelopers #".concat(event.target.value.split("$")[1])).html());
    });

    $("#customEpicsCreateForm").on("submit", event => {
        event.preventDefault();

        const form = $(event.target);
        const formData = {"userStories": []};

        form.find('sl-input, sl-select, sl-textarea').each(function () {
            formData[this.name] = this.value;
        });

        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];

        formData.epicCreatedOn = formatDateTime(formData.epicCreatedOn);

        customData.push(formData)

        sessionStorage.setItem("customData", JSON.stringify(customData));

        notifySuccess("New epic added. Total epics present: " + customData.length);

        form.trigger('reset');

        updateCustomEpicsList();
    });

    updateCustomEpicsList();

    $(document).on("click", ".removeCustomEpicSlButton", async (event) => { //Needed for dynamically created elements
        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];
        customData.splice(event.target.value, 1);
        sessionStorage.setItem("customData", JSON.stringify(customData));

        updateCustomEpicsList();
    });

    $("#customUserStoriesCreateForm").on("submit", event => {
        event.preventDefault();

        const form = $(event.target);
        const formData = {"technicalTasks": []};

        form.find('sl-input, sl-select, sl-textarea').each(function () {
            formData[this.name] = this.value;
        });

        formData.userStoryCreatedOn = formatDateTime(formData.userStoryCreatedOn);
        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];
        console.log(formData);
        const selectedEpic = formData.selectedEpic;
        delete formData.selectedEpic;
        formData.selectedEpicIndex = selectedEpic.split("$")[0];
        formData.selectedEpicDevelopmentTeam = selectedEpic.split("$")[1];
        const epic = customData[formData.selectedEpicIndex];
        epic.userStories.push(formData)
        customData[formData.selectedEpicIndex] = epic;
        sessionStorage.setItem("customData", JSON.stringify(customData));

        notifySuccess("New user story added to epic: " + epic.epicName);

        form.trigger('reset');

        updateCustomEpicsList();
    });

    $(document).on("click", ".removeCustomUserStorySlButton", async (event) => {
        let customData = JSON.parse(sessionStorage.getItem("customData"));

        let buttonValues = event.target.value.split("$");

        let currentEpic = customData[buttonValues[0]];
        currentEpic.userStories.splice(buttonValues[1], 1);
        customData[buttonValues[0]] = currentEpic;
        sessionStorage.setItem("customData", JSON.stringify(customData));

        updateCustomEpicsList();
    });

    $("#technicalTasksCreateForm").on("submit", event => {
        event.preventDefault();

        const form = $(event.target);
        const formData = {};

        form.find('sl-input, sl-select, sl-textarea').each(function () {
            formData[this.name] = this.value;
        });

        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];
        formData.technicalTaskCreatedOn = formatDateTime(formData.technicalTaskCreatedOn);
        const selectedUserStory = formData.selectedUserStory;
        delete formData.selectedUserStory;
        formData.selectedEpicIndex = selectedUserStory.split("$")[0].split(">")[0];
        formData.selectedUserStoryIndex = selectedUserStory.split("$")[0].split(">")[1];
        formData.selectedEpicDevelopmentTeam = selectedUserStory.split("$")[1];
        const epic = customData[formData.selectedEpicIndex];
        const userStory = epic.userStories[formData.selectedUserStoryIndex];
        userStory.technicalTasks.push(formData);
        epic.userStories[formData.selectedUserStoryIndex] = userStory;
        customData[formData.selectedEpicIndex] = epic;
        sessionStorage.setItem("customData", JSON.stringify(customData));

        notifySuccess("New technical task added to user story: " + userStory.userStoryName);

        form.trigger('reset');

        updateCustomEpicsList();
    });

    $(document).on("click", ".removeCustomTechnicalTaskSlButton", async (event) => {
        let customData = JSON.parse(sessionStorage.getItem("customData"));

        const buttonValues = event.target.value.split("$");


        let currentEpic = customData[buttonValues[0]];
        let currentUserStory = currentEpic.userStories[buttonValues[1]];
        console.log(currentUserStory.technicalTasks);
        currentUserStory.technicalTasks.splice(buttonValues[2], 1);
        currentEpic.userStories[buttonValues[1]] = currentUserStory;
        customData[buttonValues[0]] = currentEpic;
        console.log(customData);
        sessionStorage.setItem("customData", JSON.stringify(customData));

        updateCustomEpicsList();
    });

    $("#saveToSessionFileSlButton").on("click", () => {
        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];

        if (customData.length === 0) notifyWarning("You haven`t added any epic, user story nor technical task. No data provided for saving!")
        else {
            customData.forEach(epic => epic.userStories.forEach(userStory => {
                delete userStory.selectedEpicIndex;
                userStory.technicalTasks.forEach(technicalTask => {
                    delete technicalTask.selectedEpicIndex;
                    delete technicalTask.selectedUserStoryIndex;
                });
            }));

            $.ajax({
                type: "POST",
                url: "/api/saveSessionData",
                contentType: 'application/json',
                data: JSON.stringify(customData),
                success: response => {
                    notifySuccess(response);
                }
            });
        }
    });

    $("#loadFromSavedSessionFileSlButton").on("click", async () => {
        $.ajax({
            type: "GET",
            url: "/api/getPredefinedDataFoldersList",
            success: response => {
                let slRadioOptions= "";
                let counter = 1;
                response.forEach(folderName => {
                    slRadioOptions += `<sl-radio name="predefinedDataSelection" value="${folderName}">${counter++}. ${folderName}</sl-radio>`;
                });

                $("#predefinedDataSelection > div").html(`
                        <form id="predefinedDataSelectionForm" method="POST" style="text-align: -webkit-center;">
                            <sl-radio-group label="Select predefined data folder" name="predefinedDataSelection">
                              ${slRadioOptions}
                            </sl-radio-group>
                            <sl-divider></sl-divider>
                            <div style="display: inline-flex">
                                <sl-button id="getPredefinedDataFoldersListConfirmSlButton" outline type="submit" variant="success">Confirm selection</sl-button>
                                <sl-divider vertical></sl-divider>
                                <sl-button outline type="reset" variant="neutral" onclick="$('#predefinedDataSelection')[0].hide()">Clear form</sl-button>
                            </div>
                        </form>
                    `);

                $('#predefinedDataSelection')[0].show();
            }
        });
    });

    $(document).on("submit", "#predefinedDataSelectionForm", async (event) => {
        event.preventDefault();

        $.ajax({
            type: "GET",
                url: "/api/loadSessionData?folder=".concat($("sl-radio-group[name='predefinedDataSelection']")[0].value),
            success: response => {
                sessionStorage.setItem("customData", response);
                updateCustomEpicsList();
                $('#predefinedDataSelection')[0].hide();
                notifySuccess("Loaded predefined session data and respective developers team setup");
            },
            error: response => {
                $('#predefinedDataSelection')[0].hide();
                notifyError(response.responseText);
            }
        });
    });
})

function notifyInfo(message){
    notify(message, 'primary', 'info-circle',  1000);
}

function notifySuccess(message){
    notify(message, 'success', 'check2-circle',  1100);
}

function notifyWarning(message){
    notify(message, "warning", "exclamation-triangle",  1300);
}

function notifyError(message){
    notify(message, 'error', 'exclamation-octagon',  1500);
}

function notify(message, variant = 'primary', icon = 'info-circle', duration = 1000) {
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
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (array[0] + Math.random() * 16) % 16 | 0;
        array[0] = Math.floor(array[0] / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}

function appendDataToMQTTTopicDivs(topicName, data) {
    if (data.length === 0 || data === "\n") return;
    switch (topicName) {
        case "information-printout-topic": {
            const sanitizedData = sanitizeInformationData(data);
            $("#informationLogs").append(`<div>${sanitizedData.replace(/^<br\s*\/?>/, '')}</div>`);
            break;
        }
        case "java-activity-stream-printout-topic": {
            const sanitizedData = sanitizeJavaActivityStreamData(data);
            $("#jiraActivityStream div").prepend(sanitizedData.replace(/^<br\s*\/?>/, ''));
            break;
        }
        case "error-printout-topic": {
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

function updateCustomEpicsList() {
    const customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];
    const epicsViewTab = $("sl-tab-panel[name='customEpicsViewTab']");

    let isUserStoriesEmpty = true;
    let isTechnicalTasksEmpty = true;
    let technicalTaskList = [];

    if (customData == null || customData.length === 0) epicsViewTab.html(`<p>There aren't any epics created in this session</p>`);
    else {
        epicsViewTab.html(`<div id="epicsWrapper"></div>`);

        const epicSlSelect = $("sl-select[name='selectedEpic']");
        epicSlSelect.empty();
        $("sl-tab-panel[name='customEpicsViewTab'] #epicsWrapper").css({
            "display": "grid",
            "grid-template-columns": "repeat(var(--numberOfColumns), 1fr)",
            "height": "100%",
            "row-gap": "1%",
            "column-gap": "1%"
        });

        const customUserStories = $("#customUserStories")[0];
        const customTechnicalTasks = $("#customTechnicalTasks")[0];

        customData.forEach((value, key) => {
            $("#epicsWrapper").append(`<sl-card id="epic_#${key}" style="height:100%; --border-color: rgb(150, 2, 253, 1); text-align: -webkit-center;">
                <strong>ID: ${value.epicId}</strong>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Name: <i>${value.epicName}</i></span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Count of user stories: ${value.userStories.length}</span>
                <sl-divider></sl-divider>
                <span>Priority: ${$("#priorityBadges #" + value.epicPriority).html()}</span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Created on: ${value.epicCreatedOn}</span>
                <sl-divider></sl-divider>
                <sl-badge variant="danger">
                    Reporter: ${value.epicReporter === -1 ? $("#technicalManagerNameField").html().split(" (UMCN")[0] : $("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.epicReporter + "]").html()}
                </sl-badge>
                <sl-badge variant="warning">
                    Assignee: ${$("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.epicAssignee + "]").html()}
                </sl-badge>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <sl-divider></sl-divider>
                <sl-details summary="Description">
                  ${value.epicDescription}
                </sl-details>
                <div slot="footer">
                    <sl-button class="editCustomEpicSlButton" variant="warning" outline>Edit</sl-button>
                    <sl-divider style="--spacing: 2px" vertical></sl-divider>
                    <sl-button class="removeCustomEpicSlButton" variant="danger" value="${key}" outline>Remove</sl-button>
                </div>
            </sl-card>`);

            epicSlSelect.append(`<sl-option value="${key + "$" + value.selectedEpicDevelopmentTeam}">${value.epicName}</sl-option>`);

            value.userStories.forEach(userStory => {
                if(isUserStoriesEmpty){
                    $("sl-tab-panel[name='customUserStoriesViewTab']").html(`<sl-carousel pagination></sl-carousel>`);

                    const userStoriesCarousel = $("sl-tab-panel[name='customUserStoriesViewTab'] sl-carousel");
                    userStoriesCarousel.css("--aspect-ratio", "0");

                    setTimeout(() => $("#scroll-container", userStoriesCarousel[0].shadowRoot).css("overflow-y", "auto"), 1000);

                    $(userStoriesCarousel).on('sl-slide-change', event => {
                        $(customUserStories.shadowRoot).find("#title slot").html($(customUserStories.shadowRoot).find("#title slot").html().split("(")[0] + "(Currently viewing Epic: '" + $(event.target.children).filter((index, child) => $(child).attr('class') && $(child).attr('class').includes('--is-active'))[0].id.split("Of")[1] + "')");
                    });

                    isUserStoriesEmpty = false;
                }

                userStory.technicalTasks.forEach(technicalTask => {
                    if(isTechnicalTasksEmpty){
                        $("sl-tab-panel[name='customTechnicalTasksViewTab']").html(`<sl-carousel pagination></sl-carousel>`);

                        const technicalTasksCarousel = $("sl-tab-panel[name='customTechnicalTasksViewTab'] sl-carousel");
                        technicalTasksCarousel.css("--aspect-ratio", "0");

                        setTimeout(() => $("#scroll-container", technicalTasksCarousel[0].shadowRoot).css("overflow-y", "auto"), 1000);

                        $(technicalTasksCarousel).on('sl-slide-change', event => {
                            $(customTechnicalTasks.shadowRoot).find("#title slot").html($(customTechnicalTasks.shadowRoot).find("#title slot").html().split("(")[0] + "(Currently viewing User story: '" + $(event.target.children).filter((index, child) => $(child).attr('class') && $(child).attr('class').includes('--is-active'))[0].id.split("Of")[1] + "')");
                        });

                        isTechnicalTasksEmpty = false;
                    }

                    technicalTaskList.push(technicalTask);
                });

                if(technicalTaskList.length > 0) {
                    updateCustomTechnicalTasksList(userStory.userStoryId, technicalTaskList);
                    technicalTaskList = [];
                }
            });

            updateCustomUserStoriesList(value.epicId, value.userStories);
        });
    }

    if(isUserStoriesEmpty) $("sl-tab-panel[name='customUserStoriesViewTab']").html(`<p>There aren't any user stories created in this session</p>`);
    if(isTechnicalTasksEmpty) $("sl-tab-panel[name='customTechnicalTasksViewTab']").html(`<p>There aren't any technical tasks created in this session</p>`);
}

function updateCustomUserStoriesList(relatedEpicId, userStories) {
    const userStoriesCarousel = $("sl-tab-panel[name='customUserStoriesViewTab'] sl-carousel");

    userStoriesCarousel.append(`<sl-carousel-item id="userStoriesOf${relatedEpicId}" style="display: grid; grid-template-columns: repeat(var(--numberOfColumns), 1fr); height: 100%; row-gap: 1%; column-gap: 1%; text-align: -webkit-center;"></sl-carousel-item>`)

    const userStorySlSelect = $("sl-select[name='selectedUserStory']");

    userStorySlSelect.empty();

    userStories.forEach((value, key) => {
        $("#userStoriesOf" + relatedEpicId).append(`
            <sl-card id="${key}" style="height:100%; --border-color: rgb(130, 244, 131, 1)">
                <strong>ID: ${value.userStoryId}</strong>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Name: <i>${value.userStoryName}</i></span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Count of technical tasks: ${value.technicalTasks.length}</span>
                <sl-divider></sl-divider>
                <span>Priority: ${$("#priorityBadges #" + value.userStoryPriority).html()}</span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Created on: ${value.userStoryCreatedOn}</span>
                <sl-divider></sl-divider>
                <sl-badge variant="danger">
                    Reporter: ${$("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.userStoryReporter + "]").html()}
                </sl-badge>
                <sl-badge variant="warning">
                    Assignee: ${$("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.userStoryAssignee + "]").html()}
                </sl-badge>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <sl-divider></sl-divider>
                <sl-details summary="Description">
                  ${value.userStoryDescription}
                </sl-details>
                <div slot="footer">
                    <sl-button class="editCustomUserStorySlButton" variant="warning" outline>Edit</sl-button>
                    <sl-divider style="--spacing: 2px" vertical></sl-divider>
                    <sl-button class="removeCustomUserStorySlButton" variant="danger" value="${value.selectedEpicIndex}\$${key}" outline>Remove</sl-button>
                </div>
            </sl-card>
        `);

        userStorySlSelect.append(`<sl-option value="${value.selectedEpicIndex + ">" + key + "$" + value.selectedEpicDevelopmentTeam}">${value.userStoryName}</sl-option>`);
    });
}

function updateCustomTechnicalTasksList(relatedUserStoryId, technicalTasks) {
    const technicalTaskCarousel = $("sl-tab-panel[name='customTechnicalTasksViewTab'] sl-carousel");

    technicalTaskCarousel.append(`<sl-carousel-item id="technicalTaskOf${relatedUserStoryId}" style="display: grid; grid-template-columns: repeat(var(--numberOfColumns), 1fr); height: 100%; row-gap: 1%; column-gap: 1%; text-align: -webkit-center;"></sl-carousel-item>`)

    setTimeout(() => $($("#customTechnicalTasks")[0].shadowRoot).find("#title slot").html("Manage technical tasks for predefined application flow (Currently viewing User story: '" + relatedUserStoryId + "')"), 500);

    technicalTasks.forEach((value, key) => {
        $("#technicalTaskOf" + relatedUserStoryId).append(`
            <sl-card id="${key}" style="height:100%; --border-color: rgb(124, 124, 124, 1)">
                <strong>ID: ${value.technicalTaskId}</strong>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Name: <i>${value.technicalTaskName}</i></span>
                <sl-divider></sl-divider>
                <span>Priority: ${$("#priorityBadges #" + value.technicalTaskPriority).html()}</span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Created on: ${value.technicalTaskCreatedOn}</span>
                <sl-divider></sl-divider>
                <sl-badge variant="danger">
                    Reporter: ${$("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.technicalTaskReporter + "]").html()}
                </sl-badge>
                <sl-badge variant="warning">
                    Assignee: ${$("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.technicalTaskAssignee + "]").html()}
                </sl-badge>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <sl-divider></sl-divider>
                <sl-details summary="Description">
                  ${value.technicalTaskDescription}
                </sl-details>
                <div slot="footer">
                    <sl-button class="editCustomTechnicalTaskSlButton" variant="warning" outline>Edit</sl-button>
                    <sl-divider style="--spacing: 2px" vertical></sl-divider>
                    <sl-button class="removeCustomTechnicalTaskSlButton" variant="danger" value="${value.selectedEpicIndex}\$${value.selectedUserStoryIndex}\$${key}" outline>Remove</sl-button>
                </div>
            </sl-card>
        `);
    });
}

function formatDateTime(datetimeLocalValue) {
    const date = new Date(datetimeLocalValue);

    if (isNaN(date.getTime())) { // Check for invalid date
        return 'Invalid Date';
    }

    const pad = (num) => num.toString().padStart(2, '0');

    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1);
    const year = date.getFullYear();
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());

    return `${day}.${month}.${year}. ${hours}:${minutes}:${seconds}`;
}