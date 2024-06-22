$(window).on("load", async () => {
    await Promise.all([
        customElements.whenDefined("sl-tab-panel"),
        customElements.whenDefined("sl-carousel"),
    ]).then(() => {
        setupResize();

        $($("#sl-tab-panel-1 sl-carousel")[0].shadowRoot).find("#scroll-container")[0].style.setProperty("overflow-y", "auto");

        $('.sl-rating-developer').each((index, slRating) => slRating.getSymbol = (() => '<sl-icon name="code-slash"></sl-icon>')); //Change icon for every sl-rating with class .rating-developers
        $(".developer-experience-range")[0].tooltipFormatter = value => `Developer experience - ${value}/10`;

        $("#developmentTeamsSelectionTree")[0].addEventListener("sl-selection-change", () => {
            $("#selectedDevelopmentTeamIndex")[0].value = parseInt($("#developmentTeamsSelectionTree [selected]")[0].id);
        });

        $($("#sl-tab-panel-3")[0]).on("sl-selection-change", "#editDeveloperDevelopmentTeamsSelectionTree", () => {
            $("#editDeveloperSelectedDevelopmentTeamIndex")[0].value = parseInt($("#editDeveloperDevelopmentTeamsSelectionTree [selected]")[0].id);
        });

        $(".edit-developer-button").each((index, button) => button.addEventListener("click", async e => {
            const viewTab =  $("#sl-tab-1")[0];
            const createTab =  $("#sl-tab-2")[0];
            const editTab =  $("#sl-tab-3")[0];
            const recreateTab =  $("#sl-tab-4")[0];

            window.history.replaceState(null, null, "/developers/edit?".concat(e.currentTarget.value));
            $.ajax({
                type: "GET",
                url: "/developers/edit?".concat(e.currentTarget.value),
                success: response => {
                    $("#sl-tab-panel-3")[0].innerHTML = response
                    $(".edit-developer-reset-button")[0].addEventListener("click", async () => {
                        window.history.replaceState(null, null, "/developers");
                        viewTab.disabled = false;
                        createTab.disabled = false;
                        editTab.disabled = true;
                        recreateTab.disabled = false;
                        $("#sl-tab-panel-3")[0].innerHTML = "";

                        await Promise.all([!viewTab.disabled]).then(() => $("body sl-tab-group")[0].show("tab-developers-view"));
                    });
                }
            });

            viewTab.disabled = true;
            createTab.disabled = true;
            editTab.disabled = false;
            recreateTab.disabled = true;

            await Promise.all([!editTab[0].disabled]).then(() => $("body sl-tab-group")[0].show("tab-developers-edit"));
        }));
    });
})

function setupResize() {
    const viewportBreakpointQuery1 = window.matchMedia('(min-width: 2101px)');
    const viewportBreakpointQuery2 = window.matchMedia('(max-width: 2100px)');
    const viewportBreakpointQuery3 = window.matchMedia('(max-width: 1920px)');
    const viewportBreakpointQuery4 = window.matchMedia('(max-width: 1500px)');
    const viewportBreakpointQuery5 = window.matchMedia('(max-width: 1100px)');
    const viewportBreakpointQuery6 = window.matchMedia('(max-width: 700px)');

    viewportBreakpointQuery1.addEventListener('change', (event) => layoutChangedCallback1(event.matches));
    viewportBreakpointQuery2.addEventListener('change', (event) => layoutChangedCallback2(event.matches));
    viewportBreakpointQuery3.addEventListener('change', (event) => layoutChangedCallback3(event.matches));
    viewportBreakpointQuery4.addEventListener('change', (event) => layoutChangedCallback4(event.matches));
    viewportBreakpointQuery5.addEventListener('change', (event) => layoutChangedCallback5(event.matches));
    viewportBreakpointQuery6.addEventListener('change', (event) => layoutChangedCallback6(event.matches));

    const bodySpan = $('body span')[0];
    const bodySpanA = $('body span a')[0]

    const callbackDuplicates = () => {
        $('.sl-carousel-custom-modification')[0].style.setProperty("--aspect-ratio", 16 / 9);
        bodySpan.style.setProperty("left", "2vw");
        bodySpan.style.setProperty("top", "1.5%");
        bodySpanA.style.setProperty("font-size", "var(--sl-font-size-medium)");
        $(bodySpanA).html($(bodySpanA).html().replace("&nbsp;","<br>"));
        $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-flex");
    }

    const layoutChangedCallback1 = (matches) => {
        if (matches) {
            $(':root')[0].style.setProperty('--numberOfColumns', 6);
            callbackDuplicates();
            $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-flex");
        }
    }

    const layoutChangedCallback2 = (matches) => {
        if (matches) {
            $(':root')[0].style.setProperty('--numberOfColumns', 5);
            callbackDuplicates();
            $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-flex");
        } else layoutChangedCallback1(viewportBreakpointQuery1.matches);
    }

    const layoutChangedCallback3 = (matches) => {
        if (matches) {
            $(':root')[0].style.setProperty('--numberOfColumns', 4);
            callbackDuplicates();
            $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-flex");
        } else layoutChangedCallback2(viewportBreakpointQuery2.matches);
    }

    const layoutChangedCallback4 = (matches) => {
        if (matches) {
            $(':root')[0].style.setProperty('--numberOfColumns', 3);
            $('.sl-carousel-custom-modification')[0].style.setProperty("--aspect-ratio", 4 / 3);
            bodySpan.style.setProperty("left", "2vw");
            bodySpan.style.setProperty("top", "2.5%");
            bodySpanA.style.setProperty("font-size", "var(--sl-font-size-small)");
            $(bodySpanA).html($(bodySpanA).html().replace("&nbsp;","<br>"));
            $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-flex");
        } else layoutChangedCallback3(viewportBreakpointQuery3.matches);
    }

    const layoutChangedCallback5 = (matches) => {
        if (matches) {
            $(':root')[0].style.setProperty('--numberOfColumns', 2);
            $('.sl-carousel-custom-modification')[0].style.setProperty("--aspect-ratio", 0.75);
            bodySpan.style.setProperty("top", "1.5%");
            bodySpan.style.setProperty("left", "30%");
            bodySpanA.style.setProperty("font-size", "var(--sl-font-size-2x-small)");
            $(bodySpanA).html($(bodySpanA).html().replace("&nbsp;","<br>"));
            $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-flex");
        } else layoutChangedCallback4(viewportBreakpointQuery4.matches);
    }

    const layoutChangedCallback6 = (matches) => {
        if (matches) {
           $(':root')[0].style.setProperty('--numberOfColumns', 1);
           $('.sl-carousel-custom-modification')[0].style.setProperty("--aspect-ratio", 0.75);
           bodySpan.style.setProperty("top", "1.5%");
           bodySpan.style.setProperty("left", "23%");
           bodySpanA.style.setProperty("font-size", "var(--sl-font-size-2x-small)");
           $(bodySpanA).html($(bodySpanA).html().replace("&nbsp;","<br>"));
           $("#developer-create-form div:nth-child(1)")[0].style.setProperty("display", "inline-block");
        } else layoutChangedCallback5(viewportBreakpointQuery5.matches);
    }

    layoutChangedCallback6(viewportBreakpointQuery6.matches);
}