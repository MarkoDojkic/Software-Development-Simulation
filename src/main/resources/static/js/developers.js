$(window).on("load", async () => {
    await Promise.all([
        customElements.whenDefined("sl-tab-panel"),
        customElements.whenDefined("sl-carousel"),
    ]).then(() => {
        onResize();

        $("#sl-tab-panel-1 > sl-carousel > #scroll-container").style.setProperty("overflow-y", "auto");

        $('.rating-developers').forEach(value => value.getSymbol = (() => '<sl-icon name="code-slash"></sl-icon>')); //Change icon for every sl-rating with class .rating-developers
        $(".user-experience-range").tooltipFormatter = value => `Developer experience - ${value}/10`;

        $("#developmentTeamsSelectionTree").addEventListener("sl-selection-change", event => {
            $("#selectedDevelopmentTeamIndex").value = parseInt($("#developmentTeamsSelectionTree").querySelector('[selected]').id);
        });

        $("#sl-tab-panel-3").on("sl-selection-change", "#editDeveloperDevelopmentTeamsSelectionTree", () => {
            $("#editDeveloperSelectedDevelopmentTeamIndex").value = parseInt($("#editDeveloperDevelopmentTeamsSelectionTree").querySelector('[selected]').id);
        });

        $(".edit-developer-button").forEach(editButton => editButton.addEventListener("click", async e => {
            window.history.replaceState(null, null, "/developers/edit?".concat(e.currentTarget.value));
            $.ajax({
                type: "GET",
                url: "/developers/edit?".concat(e.currentTarget.value),
                success: response => {
                    $("#sl-tab-panel-3").innerHTML = response
                }
            });

            $("#sl-tab-1").disabled = true;
            $("#sl-tab-2").disabled = true;
            $("#sl-tab-3").disabled = false;
            $("#sl-tab-4").disabled = true;

            await Promise.all([!$("#sl-tab-3").disabled]).then(() => $("body > sl-tab-group").show("tab-developers-edit"));
        }));
    });
})

function onResize() {
    if (outerWidth > 2100) {
        $(':root').style.setProperty('--numberOfColumns', 6);
        $('.custom-modifications').style.setProperty("--aspect-ratio", 16 / 9);
    } else if (outerWidth <= 2100 && outerWidth > 1920) {
        $(':root').style.setProperty('--numberOfColumns', 5);
        $('.custom-modifications').style.setProperty("--aspect-ratio", 16 / 9);
    } else if (outerWidth <= 1920 && outerWidth > 1500) {
        $(':root').style.setProperty('--numberOfColumns', 4);
        $('.custom-modifications').style.setProperty("--aspect-ratio", 16 / 9);
    } else if (outerWidth <= 1500 && outerWidth > 1100) {
        $(':root').style.setProperty('--numberOfColumns', 3);
        $('.custom-modifications').style.setProperty("--aspect-ratio", 4 / 3);
    } else if (outerWidth <= 1100 && outerWidth > 800) {
        $(':root').style.setProperty('--numberOfColumns', 2);
        $('.custom-modifications').style.setProperty("--aspect-ratio", 0.75);
    } else if (outerWidth <= 800) {
        $(':root').style.setProperty('--numberOfColumns', 1);
        $('.custom-modifications').style.setProperty("--aspect-ratio", 0.5);
    }
}

async function cancelEditDeveloper() {
    $("#sl-tab-1").disabled = false;
    $("#sl-tab-2").disabled = false;
    $("#sl-tab-3").disabled = true;
    $("#sl-tab-4").disabled = false;

    await Promise.all([!$("#sl-tab-1").disabled]).then(() => $("body > sl-tab-group").show("tab-developers-view"));
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
    $("#developmentTeamsSelectionTree").querySelector('[selected]').removeAttribute("selected");
}