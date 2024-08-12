$(window).on("load", async () => {
    await Promise.all([
        customElements.whenDefined("sl-tab-panel"),
        customElements.whenDefined("sl-carousel"),
    ]).then(() => {
        setupResize();

        $($("#sl-tab-panel-1 sl-carousel")[0].shadowRoot).find("#scroll-container").css("overflow-y", "auto");

        $('.sl-rating-developer').each((index, slRating) => slRating.getSymbol = (() => '<sl-icon name="code-slash"></sl-icon>')); //Change icon for every sl-rating with class .rating-developers
        $(".developerExperienceSlRange").each((index, slRange) => slRange.tooltipFormatter = value => `Developer experience - ${value}/10`); //Change tooltip message on each slRange instances*

        $("#developmentTeamsSelectionTree").on("sl-selection-change", event => { //Mimic select element on tree element
            $("#selectedDevelopmentTeamIndex").val(parseInt($(event.originalEvent.detail.selection[0]).attr("id")));
        });

        $("#sl-tab-panel-3").on("sl-selection-change", "#editDeveloperDevelopmentTeamsSelectionTree", event => { //Mimic select element on tree element
            $("#editDeveloperSelectedDevelopmentTeamIndex").val(parseInt($(event.originalEvent.detail.selection[0]).attr("id")));
        });

        //Below is referenced via $(document) since they are dynamically created buttons
        $(document).on("click", ".editDeveloperSlButton", async function (){
            const developmentTeamIndex = $(this).data("development-team-index");
            const developerIndex = $(this).data("developer-index");

            const viewTab =  $("#sl-tab-1");
            const createTab =  $("#sl-tab-2");
            const editTab =  $("#sl-tab-3");
            const recreateTab =  $("#sl-tab-4");

            window.history.replaceState(null, null, `/developers/edit?developmentTeamIndex=${developmentTeamIndex}&developerIndex=${developerIndex}`);
            $.ajax({
                type: "GET",
                url: `/developers/edit?developmentTeamIndex=${developmentTeamIndex}&developerIndex=${developerIndex}`,
                success: response => {
                    $("#sl-tab-panel-3").html(response);
                    $("#sl-tab-panel-3 .developerExperienceSlRange")[0].tooltipFormatter = value => `Developer experience - ${value}/10`; //*Here needs to be reinitialize since new sl-range is created
                    $(document).on("click", ".editDeveloperResetSlButt", async () => {
                        window.history.replaceState(null, null, "/developers");
                        viewTab.prop("disabled", false);
                        createTab.prop("disabled", false)
                        editTab.prop("disabled", true)
                        recreateTab.prop("disabled", false)
                        $("#sl-tab-panel-3").empty();

                        await Promise.all([!viewTab.prop("disabled")]).then(() => $("body sl-tab-group")[0].show("tabDevelopersView"));
                    });
                }
            });

            viewTab.prop("disabled", true)
            createTab.prop("disabled", true)
            editTab.prop("disabled", false)
            recreateTab.prop("disabled", true)

            await Promise.all([!editTab.prop("disabled")]).then(() => $("body sl-tab-group")[0].show("tabDevelopersEdit"));
        });

        $(document).on('click', '.removeDeveloperSlButton', function() {
            const currentRemoveSlButton = $(this);
            const otherDevelopersFooterSlButtons = currentRemoveSlButton.parent().parent().nextAll('sl-card').find("developerExperienceSlRange slSlButt");
            const developmentTeamIndex = currentRemoveSlButton.data("development-team-index");
            const developerIndex = currentRemoveSlButton.data("developer-index");

            $.ajax({
                type: 'DELETE',
                async: true,
                url: `/api/removeDeveloper?developmentTeamIndex=${developmentTeamIndex}&developerIndex=${developerIndex}`,
                success: function() {
                    if (developerIndex === 0 && otherDevelopersFooterSlButtons.length === 0) { //If is only one remove sl-carousel-item of removed developer's team
                        currentRemoveSlButton.parent().parent().parent().nextAll('sl-carousel-item').each(function(index, slCarouselItem) {
                            $(slCarouselItem).children('sl-card').find("div[slot='footer'] sl-button").each(function(index, slButton) {
                                $(slButton).data("development-team-index", $(slButton).data("development-team-index") - 1);
                            });
                        }); //Update indexes for fallowing development teams
                        currentRemoveSlButton.parent().parent().parent().remove();
                    } else { //Update indexes for fallowing developers
                        otherDevelopersFooterSlButtons.each(function(index, slButton) {
                            $(slButton).data("developer-index", $(slButton).data("developer-index") - 1);
                        });
                    }

                    currentRemoveSlButton.parent().parent().remove(); //Remove sl-card of removed developer
                }
            });
        });
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

    const bodySpan = $('body span').first();
    const bodySpanA = $('body span a').first();

    const callbackDuplicates = () => {
        bodySpan.css("left", "2vw");
        bodySpan.css("top", "1.5%");
        bodySpanA.css("font-size", "var(--sl-font-size-medium)");
        bodySpanA.html(bodySpanA.html().replaceAll("&nbsp;","<br>"));
        $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
    }

    const layoutChangedCallback1 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 6);
            callbackDuplicates();
            $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        }
    }

    const layoutChangedCallback2 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 5);
            callbackDuplicates();
            $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        } else layoutChangedCallback1(viewportBreakpointQuery1.matches);
    }

    const layoutChangedCallback3 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 4);
            callbackDuplicates();
            $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        } else layoutChangedCallback2(viewportBreakpointQuery2.matches);
    }

    const layoutChangedCallback4 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 3);
            bodySpan.css("left", "2vw");
            bodySpan.css("top", "2.5%");
            bodySpanA.css("font-size", "var(--sl-font-size-small)");
            bodySpanA.html(bodySpanA.html().replaceAll("&nbsp;","<br>"));
            $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        } else layoutChangedCallback3(viewportBreakpointQuery3.matches);
    }

    const layoutChangedCallback5 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 2);
            bodySpan.css("top", "1.5%");
            bodySpan.css("left", "30%");
            bodySpanA.css("font-size", "var(--sl-font-size-2x-small)");
            bodySpanA.html(bodySpanA.html().replaceAll("&nbsp;","<br>"));
            $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        } else layoutChangedCallback4(viewportBreakpointQuery4.matches);
    }

    const layoutChangedCallback6 = (matches) => {
        if (matches) {
           $(':root').css('--numberOfColumns', 1);
           bodySpan.css("top", "1.5%");
           bodySpan.css("left", "23%");
           bodySpanA.css("font-size", "var(--sl-font-size-2x-small)");
           bodySpanA.html(bodySpanA.html().replaceAll("&nbsp;","<br>"));
           $("#developer-create-form div:nth-child(1)").css("display", "inline-block");
        } else layoutChangedCallback5(viewportBreakpointQuery5.matches);
    }

    layoutChangedCallback6(viewportBreakpointQuery6.matches);
}