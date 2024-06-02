$(window).on("load", async () => {
    await Promise.all([
        customElements.whenDefined("sl-tab-panel"),
        customElements.whenDefined("sl-carousel"),
    ]).then(() => {
        onResize();

        $($("#sl-tab-panel-1 sl-carousel")[0].shadowRoot).find("#scroll-container")[0].style.setProperty("overflow-y", "auto");

        $('.rating-developers').each(value => value.getSymbol = (() => '<sl-icon name="code-slash"></sl-icon>')); //Change icon for every sl-rating with class .rating-developers
        $(".user-experience-range")[0].tooltipFormatter = value => `Developer experience - ${value}/10`;

        $("#developmentTeamsSelectionTree")[0].addEventListener("sl-selection-change", event => {
            $("#selectedDevelopmentTeamIndex")[0].value = parseInt($("#developmentTeamsSelectionTree [selected]")[0].id);
        });

        $($("#sl-tab-panel-3")[0]).on("sl-selection-change", "#editDeveloperDevelopmentTeamsSelectionTree", () => {
            $("#editDeveloperSelectedDevelopmentTeamIndex")[0].value = parseInt($("#editDeveloperDevelopmentTeamsSelectionTree [selected]")[0].id);
        });

        $(".edit-developer-button").each((index, button) => button.addEventListener("click", async e => {
            window.history.replaceState(null, null, "/developers/edit?".concat(e.currentTarget.value));
            $.ajax({
                type: "GET",
                url: "/developers/edit?".concat(e.currentTarget.value),
                success: response => {
                    $("#sl-tab-panel-3")[0].innerHTML = response
                }
            });

            $("#sl-tab-1")[0].disabled = true;
            $("#sl-tab-2")[0].disabled = true;
            $("#sl-tab-3")[0].disabled = false;
            $("#sl-tab-4")[0].disabled = true;

            await Promise.all([!$("#sl-tab-3")[0].disabled]).then(() => $("body sl-tab-group")[0].show("tab-developers-edit"));
        }));
    });
})

function onResize(_event) {
    if (outerWidth > 2100) {
        $(':root')[0].style.setProperty('--numberOfColumns', 6);
        $('.custom-modifications')[0].style.setProperty("--aspect-ratio", 16 / 9);
    } else if (outerWidth <= 2100 && outerWidth > 1920) {
        $(':root')[0].style.setProperty('--numberOfColumns', 5);
        $('.custom-modifications')[0].style.setProperty("--aspect-ratio", 16 / 9);
    } else if (outerWidth <= 1920 && outerWidth > 1500) {
        $(':root')[0].style.setProperty('--numberOfColumns', 4);
        $('.custom-modifications')[0].style.setProperty("--aspect-ratio", 16 / 9);
    } else if (outerWidth <= 1500 && outerWidth > 1100) {
        $(':root')[0].style.setProperty('--numberOfColumns', 3);
        $('.custom-modifications')[0].style.setProperty("--aspect-ratio", 4 / 3);
    } else if (outerWidth <= 1100 && outerWidth > 800) {
        $(':root')[0].style.setProperty('--numberOfColumns', 2);
        $('.custom-modifications')[0].style.setProperty("--aspect-ratio", 0.75);
    } else if (outerWidth <= 800) {
        $(':root')[0].style.setProperty('--numberOfColumns', 1);
        $('.custom-modifications')[0].style.setProperty("--aspect-ratio", 0.5);
    }
}

async function cancelEditDeveloper() {
    window.history.replaceState(null, null, "/developers");
    $("#sl-tab-1")[0].disabled = false;
    $("#sl-tab-2")[0].disabled = false;
    $("#sl-tab-3")[0].disabled = true;
    $("#sl-tab-4")[0].disabled = false;

    await Promise.all([!$("#sl-tab-1")[0].disabled]).then(() => $("body sl-tab-group")[0].show("tab-developers-view"));
}

function deleteDeveloper(developmentTeamIndex, developerIndex) {
    $.ajax({
        type: "DELETE",
        async: false,
        url: "/developers?developmentTeamIndex=" + developmentTeamIndex + "&developerIndex=" + developerIndex,
        error: () => window.location.href = "/developers"
    });
}

function clearDevelopmentTeamsSelectionTreeSelection() {
    $("#developmentTeamsSelectionTree [selected]")[0].removeAttribute("selected");
}