import 'https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.19.0/cdn/components/alert/alert.js';

$(window).on("load", async _ => {
    await Promise.all([
        customElements.whenDefined("sl-tab-panel"),
        customElements.whenDefined("sl-carousel")
    ]).then(_ => {
        setupResize();

        const themeDarkLink = $('#theme-dark');
        const themeLightLink = $('#theme-light');
        const themeSwitch = $('#theme-switch');

        themeSwitch.on('sl-change', (event) => {
            alert = Object.assign(document.createElement('sl-alert'), {
                variant: 'primary',
                closable: false,
                duration: 1000,
                innerHTML: `
                    <sl-icon name="info-circle" slot="icon"></sl-icon>
                    Theme switched to ${event.target.checked ? 'dark' : 'light'}
                `
            });

            if (event.target.checked) {
                $('html').addClass('sl-theme-dark');
                themeDarkLink.removeAttr('disabled');
                themeLightLink.attr('disabled', 'disabled');
                sessionStorage.setItem("themeDark", "true");
            } else {
                $('html').removeClass('sl-theme-dark');
                themeDarkLink.attr('disabled', 'disabled');
                themeLightLink.removeAttr('disabled');
                sessionStorage.setItem("themeDark", "false");
            }

            document.body.append(alert);
            alert.toast();
        });

        if(!sessionStorage.getItem("themeDark") || sessionStorage.getItem("themeDark") === "true") {
            const switchThemeInterval = setInterval(_ => {
                themeSwitch.click();
                sessionStorage.setItem("themeDark", "true");
                clearInterval(switchThemeInterval);
            }, 100);
        }

        $('.sl-rating-developer').each((index, slRating) => slRating.getSymbol = (_ => '<sl-icon name="code-slash"></sl-icon>')); // Change icon for every sl-rating with class .rating-developers
        $(".developerExperienceSlRange").each((index, slRange) => slRange.tooltipFormatter = value => `Developer experience - ${value}/10`); // Change tooltip message on each slRange instances

        $("#developmentTeamsSelectionTree").on("sl-selection-change", event => { // Mimic select element on tree element
            $("#selectedDevelopmentTeamIndex").val(parseInt($(event.originalEvent.detail.selection[0]).attr("id")));
        });

        $("#sl-tab-panel-3").on("sl-selection-change", "#editDeveloperDevelopmentTeamsSelectionTree", event => { // Mimic select element on tree element
            $("#editDeveloperSelectedDevelopmentTeamIndex").val(parseInt($(event.originalEvent.detail.selection[0]).attr("id")));
        });

        // Below is referenced via $(document) since they are dynamically created buttons
        $(document).on("click", ".editDeveloperSlButton", async (event) => {
            const developmentTeamIndex = $(event.target).data("development-team-index");
            const developerIndex = $(event.target).data("developer-index");

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
                    $("#sl-tab-panel-3 .developerExperienceSlRange")[0].tooltipFormatter = value => `Developer experience - ${value}/10`; // Need to be reinitialized, since new sl-range is created
                    $(document).on("click", ".editDeveloperResetSlButton", async _ => {
                        window.history.replaceState(null, null, "/developers");
                        viewTab.prop("disabled", false);
                        createTab.prop("disabled", false)
                        editTab.prop("disabled", true)
                        recreateTab.prop("disabled", false)
                        $("#sl-tab-panel-3").empty();

                        await Promise.all([!viewTab.prop("disabled")]).then(_ => $("body sl-tab-group")[0].show("tabDevelopersView"));
                    });
                }
            });

            viewTab.prop("disabled", true)
            createTab.prop("disabled", true)
            editTab.prop("disabled", false)
            recreateTab.prop("disabled", true)

            await Promise.all([!editTab.prop("disabled")]).then(_ => $("body sl-tab-group")[0].show("tabDevelopersEdit"));
        });

        $(document).on('click', '.removeDeveloperSlButton', async (event) => {
            const currentRemoveSlButton = $(event.target);
            const remainingDevelopersSlCards = currentRemoveSlButton.closest('sl-card').nextAll();
            const developmentTeamIndex = currentRemoveSlButton.data("development-team-index");
            const developerIndex = currentRemoveSlButton.data("developer-index");

            $.ajax({
                type: 'DELETE',
                async: true,
                url: `/api/deleteDeveloper?developmentTeamIndex=${developmentTeamIndex}&developerIndex=${developerIndex}`,
                success: _ => {
                    if (developerIndex === 0 && remainingDevelopersSlCards.length === 0) { // If is only one remove sl-carousel-item of removed developer's team
                        currentRemoveSlButton.closest('sl-carousel-item').nextAll().each((index, slCarouselItem) => {
                            $(slCarouselItem).find("div[slot='footer'] sl-button").each((_, otherDevelopmentTeamsFooterButton) => otherDevelopmentTeamsFooterButton.dataset.developmentTeamIndex =
                            (parseInt(otherDevelopmentTeamsFooterButton.dataset.developmentTeamIndex) - 1).toString());
                        }); // Update indexes for fallowing development teams
                        currentRemoveSlButton.closest('sl-carousel-item').remove();
                    } else { // Update indexes for fallowing developers
                        remainingDevelopersSlCards.each((index, developerSlCard) => {
                            $(developerSlCard).find("div[slot='footer'] sl-button").each((_, footerButton) => footerButton.dataset.developerIndex = (parseInt(footerButton.dataset.developerIndex) - 1).toString());
                        });
                        currentRemoveSlButton.closest('sl-card').remove(); // Remove sl-card of removed developer
                    }

                }
            });
        });
    });

    //Remove "inert" attribute on carousel that is added when scroll and fix vertical scroll
    const developersCarousel = $("#sl-tab-panel-1 sl-carousel")[0];
    $(developersCarousel.shadowRoot).find("#scroll-container").css("overflow-y", "auto");

    const observer = new MutationObserver(mutations => {
        mutations.forEach(mutation => {
            // Check if the mutation is related to the 'inert' attribute
            if ((mutation.type === 'attributes' && mutation.attributeName === 'inert') || mutation.target.hasAttribute('inert')) {
                mutation.target.removeAttribute('inert'); // Remove the 'inert' attribute
            }
        });
    });

    observer.observe(developersCarousel, { childList: true, subtree: true, attributeFilter: ["inert"] });
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

    const callbackDuplicates = _ => {
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


    const callbackDuplicatesSmall = _ => {
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