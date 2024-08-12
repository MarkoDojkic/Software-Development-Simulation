import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/alert/alert.js';
import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.15.1/cdn/components/popup/popup.js';
import {ansi2html_string} from './ansi2html.js';
//TODO: Fix issue with carousel that button (i.e. carousel item is below scroll container when scrolled and thus not interactable)
$(window).on("load", async () => {
    const websocketBroker = location.hostname;
    const websocketPort = 15675;

    const client = new Paho.MQTT.Client(websocketBroker, websocketPort, "/ws", "fe-client_".concat(generateUUID()));

    client.onConnectionLost = responseObject => {
        notify("MQTT connection lost (" + websocketBroker + ":" + websocketPort + ")<br />" + responseObject.errorMessage, "warning", "exclamation-triangle");
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
            notify("MQQT connection failure (" + websocketBroker + ":" + websocketPort + ")<br />" + message.errorMessage, "error", "exclamation-octagon");
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

    $("#generateRandomDataFlowForm #simulatePredefinedSlButton").on("click", async () => {
        if (minimalEpicsCount.attr("data-valid") !== undefined && maximalEpicsCount.attr("data-valid") !== undefined)
            $.ajax({
                type: "OPTIONS",
                url: "/api/applicationFlowRandomized?".concat("min=".concat(minimalEpicsCount.val()).concat("&max=").concat(maximalEpicsCount.val())),
                success: () => $("#generateRandomDataFlowForm").trigger('reset'),

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

        customData.push(formData)

        sessionStorage.setItem("customData", JSON.stringify(customData));

        notify("New epic added. Total epics present: " + customData.length);

        form.trigger('reset');

        updateCustomEpicsList();
    });

    updateCustomEpicsList();

    $(".removeCustomEpicSlButton").each((index, button) => $(button).on("click", async () => {
        let customData = JSON.parse(sessionStorage.getItem("customData"));
        customData.splice(index, 1);
        sessionStorage.setItem("customData", JSON.stringify(customData));

        updateCustomEpicsList();
    }));

    $("#customUserStoriesCreateForm").on("submit", event => {
        event.preventDefault();

        const form = $(event.target);
        const formData = {"technicalTasks": []};

        form.find('sl-input, sl-select, sl-textarea').each(function () {
            formData[this.name] = this.value;
        });

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

        notify("New user story added to epic: " + epic.epicName);

        form.trigger('reset');

        updateCustomEpicsList();
    });

    $(".removeCustomUserStorySlButton").each((index, button) => $(button).on("click", async () => {
        let customData = JSON.parse(sessionStorage.getItem("customData"));
        console.log($(button));

        //TODO: Finish removal of US function
        /*const currentEpic = current[this.epicIndex];
        currentEpic.splice(index, 1);
        current[this.epicIndex] = currentEpic;
        sessionStorage.setItem("customData", JSON.stringify(current));

        updateCustomEpicsList();*/
    }));

    $("#technicalTasksCreateForm").on("submit", event => {
        event.preventDefault();

        const form = $(event.target);
        const formData = {};

        form.find('sl-input, sl-select, sl-textarea').each(function () {
            formData[this.name] = this.value;
        });

        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];

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

        notify("New technical task added to user story: " + userStory.userStoryName);

        form.trigger('reset');

        updateCustomEpicsList();
    });

    $(".removeCustomTechnicalTaskSlButton").each((index, button) => $(button).on("click", async () => {
        //TODO: Finish removal of TT function
        /*const userStoriesViewTab = $("sl-tab-panel[name='customUserStoriesViewTab']");
        const current = JSON.parse(sessionStorage.getItem("customData"));
        const currentEpic = current[this.epicIndex];
        currentEpic.splice(index, 1);
        current[this.epicIndex] = currentEpic;
        sessionStorage.setItem("customData", JSON.stringify(current));
        userStoriesViewTab.children().eq(index).remove();
        if(index === 0) userStoriesViewTab.append(`<p>There aren't any user stories created in this session</p>`);*/
    }));

    $("#saveToSessionFileSlButton").on("click", () => {
        let customData = sessionStorage.getItem("customData") ? JSON.parse(sessionStorage.getItem("customData")) : [];

        if (customData.length == 0) notify("You haven`t added any epic, user story nor technical task. No data provided for saving!")
        else {
            $.ajax({
                type: "POST",
                url: "/api/saveSessionData",
                contentType: 'application/json',
                data: JSON.stringify(customData),
                success: response => {
                    notify(response);
                }
            });
        }
    });

    $("#loadFromSavedSessionFileSlButton").on("click", () => {
        //TODO: Finish load from file function
        /*$.ajax({
            type: "GET",
            url: "/api/loadSessionData?fileSuffix=...",
            success: response => {
                sessionStorage.setItem("customData", response);
            }
        });*/
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
    const currentDate = new Date();

    const year = currentDate.getFullYear(); // Full year (YYYY)
    const month = currentDate.getMonth() + 1; // Month (0-11, so we add 1 for 1-12)
    const day = currentDate.getDate(); // Day of the month (1-31)

    const customData = JSON.parse(sessionStorage.getItem('customData'));
    const epicsViewTab = $("sl-tab-panel[name='customEpicsViewTab']");

    let technicalTaskList = [];

    if (customData.length === 0) epicsViewTab.html(`<p>There aren't any epics created in this session</p>`);
    else {
        epicsViewTab.empty();

        const epicSlSelect = $("sl-select[name='selectedEpic']");
        $("sl-tab-panel[name='customEpicsViewTab'] #epicsWrapper").css("display", "grid", "grid-template-columns", "repeat(var(--numberOfColumns), 1fr)", "height", "100%", "row-gap", "1%", "column-gap", "1%");

        let isUserStoriesEmpty = true;
        let isTechnicalTasksEmpty = true;

        customData.forEach((value, key) => {
            epicsViewTab.append(`<sl-card id="epic_#${key}" style="height:100%; --border-color: rgb(150, 2, 253, 1); text-align: -webkit-center;">
                <strong>ID: ${value.epicId}</strong>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Name: <i>${value.epicName}</i></span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Count of user stories: ${value.userStories.length}</span>
                <sl-divider></sl-divider>
                <span>Priority: ${$("#priorityBadges #" + value.epicPriority).html()}</span>
                <sl-divider style="--spacing: 2px" vertical></sl-divider>
                <span>Creation time: ${day.toString().padStart(2, '0') + "." + month.toString().padStart(2, '0') + "." + year + ". " + value.epicCreatedAt}</span>
                <sl-divider></sl-divider>
                <sl-badge variant="danger">
                    Reporter: ${$("#developmentTeamsListOfDevelopers #" + value.selectedEpicDevelopmentTeam + " sl-option[value=" + value.epicReporter + "]").html()}
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

            const newEpicOption = document.createElement('sl-option');
            newEpicOption.value = key + "$" + value.selectedEpicDevelopmentTeam;
            epicSlSelect.append(newEpicOption);

            value.userStories.forEach(userStory => {
                if(isUserStoriesEmpty){
                    $("sl-tab-panel[name='customUserStoriesViewTab']").html(`<sl-carousel pagination></sl-carousel>`);

                    const userStoriesCarousel = $("sl-tab-panel[name='customUserStoriesViewTab'] sl-carousel");
                    userStoriesCarousel.css("--aspect-ratio", "0");

                    setTimeout(() => $("#scroll-container", userStoriesCarousel[0].shadowRoot).css("overflow-y", "auto"), 1000);

                    $(userStoriesCarousel).on('sl-slide-change', event => {
                        $($("#customUserStories")[0].shadowRoot).find("#title slot").html($($("#customUserStories")[0].shadowRoot).find("#title slot").html().split("(")[0] + "(Currently viewing Epic: '" + $(event.target.children).filter((index, child) => $(child).attr('class') && $(child).attr('class').includes('--is-active'))[0].id.split("Of")[1] + "')");
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
                            $($("#customTechnicalTasks")[0].shadowRoot).find("#title slot").html($($("#customTechnicalTasks")[0].shadowRoot).find("#title slot").html().split("(")[0] + "(Currently viewing User story: '" + $(event.target.children).filter((index, child) => $(child).attr('class') && $(child).attr('class').includes('--is-active'))[0].id.split("Of")[1] + "')");
                        });

                        isTechnicalTasksEmpty = false;
                    }

                    technicalTaskList.push(technicalTask);
                });

                if(technicalTaskList.length > 0) {
                    updateCustomTechnicalTasksList(day, month, year, userStory.userStoryId, technicalTaskList);
                    technicalTaskList = [];
                }
            });

            updateCustomUserStoriesList(day, month, year, value.epicId, value.userStories);
        });

        if(isUserStoriesEmpty) $("sl-tab-panel[name='customUserStoriesViewTab']").html(`<p>There aren't any user stories created in this session</p>`);
        if(isTechnicalTasksEmpty) $("sl-tab-panel[name='customTechnicalTasksViewTab']").html(`<p>There aren't any technical tasks created in this session</p>`);
    }
}

function updateCustomUserStoriesList(day, month, year, relatedEpicId, userStories) {
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
                <span>Creation time: ${day.toString().padStart(2, '0') + "." + month.toString().padStart(2, '0') + "." + year + ". " + value.userStoryCreatedAt}</span>
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
                    <sl-button class="removeCustomUserStorySlButton" variant="danger" value="${key}" outline>Remove</sl-button>
                </div>
            </sl-card>
        `);

        const newUserStoryOption = document.createElement('sl-option');
        newUserStoryOption.value = value.selectedEpicIndex + ">" + key + "$" + value.selectedEpicDevelopmentTeam;
        userStorySlSelect.append(newUserStoryOption);
    });
}

function updateCustomTechnicalTasksList(day, month, year, relatedUserStoryId, technicalTasks) {
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
                <span>Creation time: ${day.toString().padStart(2, '0') + "." + month.toString().padStart(2, '0') + "." + year + ". " + value.technicalTaskCreatedAt}</span>
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
                    <sl-button class="removeCustomTechnicalTaskSlButton" variant="danger" value="${key}" outline>Remove</sl-button>
                </div>
            </sl-card>
        `);
    });
}