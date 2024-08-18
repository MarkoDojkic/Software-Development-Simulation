$(window).on("load", async () => {
    await Promise.all([
        customElements.whenDefined("sl-tab-panel"),
        customElements.whenDefined("sl-carousel")
    ]).then(() => {
        setupResize();

        //Theme switch

        const themeDarkLink = $('#theme-dark');
        const themeLightLink = $('#theme-light');
        const themeSwitch = $('#theme-switch');

        themeSwitch.on('sl-change', (event) => {
            if (event.target.checked) {
                // Switch to dark theme
                $('html').addClass('sl-theme-dark');
                themeDarkLink.removeAttr('disabled');
                themeLightLink.attr('disabled', 'disabled');
            } else {
                // Switch to light theme
                $('html').removeClass('sl-theme-dark');
                themeDarkLink.attr('disabled', 'disabled');
                themeLightLink.removeAttr('disabled');
            }
        });

        setTimeout(() => themeSwitch.click(), 1);

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
                    $(document).on("click", ".editDeveloperResetSlButton", async () => {
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
            const otherDevelopersFooterSlButtons = currentRemoveSlButton.parent().parent().nextAll('sl-card').find(".developerExperienceSlRange slSlButt");
            const developmentTeamIndex = currentRemoveSlButton.data("development-team-index");
            const developerIndex = currentRemoveSlButton.data("developer-index");

            $.ajax({
                type: 'DELETE',
                async: true,
                url: `/api/deleteDeveloper?developmentTeamIndex=${developmentTeamIndex}&developerIndex=${developerIndex}`,
                success: function() {
                    if (developerIndex === 0 && otherDevelopersFooterSlButtons.length === 0) { //If is only one remove sl-carousel-item of removed developer's team
                        currentRemoveSlButton.parent().parent().parent().nextAll('div').each(function(index, slCarouselItem) {
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
    const viewportBreakpointQuery4 = window.matchMedia('(max-width: 1600px)');
    const viewportBreakpointQuery5 = window.matchMedia('(max-width: 1100px)');
    const viewportBreakpointQuery6 = window.matchMedia('(max-width: 980px)');

    viewportBreakpointQuery1.addEventListener('change', (event) => layoutChangedCallback1(event.matches));
    viewportBreakpointQuery2.addEventListener('change', (event) => layoutChangedCallback2(event.matches));
    viewportBreakpointQuery3.addEventListener('change', (event) => layoutChangedCallback3(event.matches));
    viewportBreakpointQuery4.addEventListener('change', (event) => layoutChangedCallback4(event.matches));
    viewportBreakpointQuery5.addEventListener('change', (event) => layoutChangedCallback5(event.matches));
    viewportBreakpointQuery6.addEventListener('change', (event) => layoutChangedCallback6(event.matches));

    const titleContainer = $('#titleContainer');

    const callbackDuplicates = () => {
        titleContainer.css({
            "left": "2vw",
            "top": "1.25%",
            "font-size": "var(--sl-font-size-medium)"
        });
        titleContainer.html(titleContainer.html().replaceAll("&nbsp;","</br>"));
        $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        if (titleContainer.next('br').length === 1) {
            titleContainer.next('br').remove();
        }
    }


    const callbackDuplicatesSmall = () => {
        titleContainer.css({
            "top": "1.25%",
            "left": "18%",
        });
        titleContainer.html(titleContainer.html().replaceAll("<br>","&nbsp;"));
        $("#developer-create-form div:nth-child(1)").css("display", "inline-flex");
        if (titleContainer.next('br').length === 0) {
            titleContainer.after('<br/>');
        }
    }

    const layoutChangedCallback1 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 6);
            callbackDuplicates();
        }
    }

    const layoutChangedCallback2 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 5);
            callbackDuplicates();
        } else layoutChangedCallback1(viewportBreakpointQuery1.matches);
    }

    const layoutChangedCallback3 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 4);
            callbackDuplicates();
        } else layoutChangedCallback2(viewportBreakpointQuery2.matches);
    }

    const layoutChangedCallback4 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 3);
            callbackDuplicatesSmall();
            titleContainer.css("left", "27vw");
        } else layoutChangedCallback3(viewportBreakpointQuery3.matches);
    }

    const layoutChangedCallback5 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', 2);
            callbackDuplicatesSmall();
        } else layoutChangedCallback4(viewportBreakpointQuery4.matches);
    }

    const layoutChangedCallback6 = (matches) => {
        if (matches) {
            $(':root').css('--numberOfColumns', "1");
            callbackDuplicatesSmall();
        } else layoutChangedCallback5(viewportBreakpointQuery5.matches);
    }

    layoutChangedCallback6(viewportBreakpointQuery6.matches);
}