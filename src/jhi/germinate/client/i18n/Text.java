/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.client.i18n;

import com.google.gwt.core.shared.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.i18n.client.impl.plurals.*;
import com.google.gwt.safehtml.shared.*;

/**
 * Main interface for the internationalization of Germinate
 *
 * @author Sebastian Raubach
 */
public interface Text extends Messages
{
	Text LANG = GWT.create(Text.class);

	@Key("general.results")
	@DefaultMessage("Results")
	String generalResults();

	@Key("general.none")
	@DefaultMessage("None")
	String generalNone();

	@Key("general.next")
	@DefaultMessage("Next")
	String generalNext();

	@Key("general.back")
	@DefaultMessage("Back")
	String generalBack();

	@Key("general.skip")
	@DefaultMessage("Skip")
	String generalSkip();

	@Key("general.done")
	@DefaultMessage("Done")
	String generalDone();

	@Key("general.select.all")
	@DefaultMessage("Select all")
	String generalSelectAll();

	@Key("general.delete")
	@DefaultMessage("Delete")
	String generalDelete();

	@Key("general.remove")
	@DefaultMessage("Remove")
	String generalRemove();

	@Key("general.add")
	@DefaultMessage("Add")
	String generalAdd();

	@Key("general.filter")
	@DefaultMessage("Filter")
	String generalFilter();

	@Key("general.search")
	@DefaultMessage("Search")
	String generalSearch();

	@Key("general.continue")
	@DefaultMessage("Continue")
	String generalContinue();

	@Key("general.minimum")
	@DefaultMessage("Minimum")
	String generalMinimum();

	@Key("general.average")
	@DefaultMessage("Average")
	String generalAverage();

	@Key("general.maximum")
	@DefaultMessage("Maximum")
	String generalMaximum();

	@Key("general.standard.deviation")
	@DefaultMessage("Standard deviation")
	String generalStandardDeviation();

	@Key("general.year")
	@DefaultMessage("Year")
	String generalYear();

	@Key("general.filename")
	@DefaultMessage("Filename")
	String generalFilename();

	@Key("general.annotations")
	String generalAnnotations();

	@Key("general.annotations.add")
	String generalAddAnnotation();

	@Key("general.annotations.enter")
	String generalEnterAnnotation();

	@Key("general.annotations.submit")
	String generalSubmitAnnotation();

	@Key("general.print")
	@DefaultMessage("Print")
	String generalPrint();

	@Key("general.date.format.short")
	String generalDateFormatShort();

	@Key("general.date.time.format")
	String generalDateTimeFormat();

	@Key("general.high")
	@DefaultMessage("High")
	String generalHigh();

	@Key("general.low")
	@DefaultMessage("Low")
	String generalLow();

	@Key("general.confirm")
	@DefaultMessage("Confirm")
	String generalConfirm();

	@Key("general.yes")
	@DefaultMessage("Yes")
	String generalYes();

	@Key("general.no")
	@DefaultMessage("No")
	String generalNo();

	@Key("general.cancel")
	@DefaultMessage("Cancel")
	String generalCancel();

	@Key("general.read.more")
	@DefaultMessage("Read more")
	String generalReadMore();

	@Key("general.count")
	@DefaultMessage("Count")
	String generalCount();

	@Key("general.upload")
	@DefaultMessage("Upload")
	String generalUpload();

	@Key("general.download")
	@DefaultMessage("Download")
	String generalDownload();

	@Key("general.click.to.edit")
	@DefaultMessage("Click to edit...")
	String generalClickToEdit();

	@Key("general.clear")
	@DefaultMessage("Clear")
	String generalClear();

	@Key("general.save.as")
	@DefaultMessage("Save as...")
	String generalSaveAs();

	@Key("general.save")
	@DefaultMessage("Save")
	String generalSave();

	@Key("general.link.get")
	String generalGetLinkToPage();

	@Key("general.link.get.title")
	String generalGetLinkToPageTitle();

	@Key("general.decline")
	@DefaultMessage("Decline")
	String generalDecline();

	@Key("general.accept")
	@DefaultMessage("Accept")
	String generalAccept();

	@Key("general.update")
	@DefaultMessage("Update")
	String generalUpdate();

	@Key("general.close")
	@DefaultMessage("Close")
	String generalClose();

	@Key("general.rename")
	@DefaultMessage("Rename")
	String generalRename();

	@Key("download.heading")
	String downloadHeading();

	@Key("download.format.google.earth")
	String downloadGoogleEarth();

	@Key("download.format.txt")
	String downloadFileAsTxt();

	@Key("download.format.html")
	@DefaultMessage("HTML")
	String downloadFileAsHtml();

	@Key("download.format.helium")
	@DefaultMessage("Download pedigree for Helium")
	String downloadPedigreeHelium();

	@Key("social.facebook")
	String socialFacebook();

	@Key("social.twitter")
	String socialTwitter();

	@Key("social.google.plus")
	String socialGooglePlus();

	@Key("menu.about")
	String menuAbout();

	@Key("menu.about.germinate")
	@DefaultMessage("about germinate")
	String menuAboutGerminate();

	@Key("menu.data.locations")
	@DefaultMessage("location data")
	String menuLocations();

	@Key("menu.data.genetic")
	@DefaultMessage("genetic data")
	String menuGenetic();

	@Key("menu.about.project")
	@DefaultMessage("about project")
	String menuAboutProject();

	@Key("menu.acknowledgements")
	String menuAcknowledgements();

	@Key("menu.allelefrequency")
	String menuAlleleFreq();

	@Key("menu.accessions.browse")
	String menuBrowseAccessions();

	@Key("menu.environment.climate")
	String menuClimate();

	@Key("menu.data")
	String menuData();

	@Key("menu.compounds")
	@DefaultMessage("compounds")
	String menuCompounds();

	@Key("menu.compound.data")
	@DefaultMessage("compound data")
	String menuCompoundData();

	@Key("menu.environment")
	String menuEnvironment();

	@Key("menu.gallery")
	String menuGallery();

	@Key("menu.genotype")
	String menuGenotypes();

	@Key("menu.geography.search")
	String menuGeographicSearch();

	@Key("menu.geography")
	String menuGeography();

	@Key("menu.groups")
	String menuGroups();

	@Key("menu.user.permissions")
	@DefaultMessage("User permissions")
	String menuUserGroups();

	@Key("menu.home")
	String menuHome();

	@Key("menu.logout")
	String menuLogout();

	@Key("menu.settings")
	@DefaultMessage("Settings")
	String menuSettings();

	@Key("menu.genotype.maps")
	String menuMaps();

	@Key("menu.environment.megaenvironment")
	String menuMegaEnvironments();

	@Key("menu.phenotypes")
	@DefaultMessage("Phenotypes")
	String menuPhenotypes();

	@Key("menu.search")
	@DefaultMessage("Search")
	String menuSearch();

	@Key("menu.trials")
	String menuTrials();

	@Key("menu.traits")
	@DefaultMessage("Traits")
	String menuTraits();

	@Key("menu.datasets.overview")
	String menuDatasetOverview();

	@Key("menu.data.statistics")
	@DefaultMessage("Data statistics")
	String menuDataStatistics();

	@Key("notification.job.long.running")
	String notificationLongRunning();

	@Key("notification.login.unsuccessful")
	String notificationLoginUnsuccessful();

	@Key("notification.permissions.insufficient")
	String notificationInsufficientPermissions();

	@Key("notification.login.user.suspended")
	String notificationLoginSuspendedUser();

	@Key("notification.login.username.password.invalid")
	@DefaultMessage("Invalid username or password.")
	String notificationLoginInvalidUsernameOrPassword();

	@Key("notification.login.missing.input")
	String notificationLoginFillFields();

	@Key("notification.login.session.invalid")
	String notificationInvalidSession();

	@Key("notification.login.cookie.invalid")
	String notificationInvalidCookie();

	@Key("notification.login.payload.invalid")
	String notificationInvalidPayload();

	@Key("notification.datasets.selection.empty")
	String notificationDatasetsSelectAtLeastOne();

	@Key("notification.attribute.selection.empty")
	@DefaultMessage("Please select at least one attribute.")
	String notificationAttributeSelectAtLeastOne();

	@Key("notification.datasets.selection.too.many")
	@DefaultMessage("At most one dataset can be selected!")
	String notificationDatasetsSelectAtMostOne();

	@Key("notification.datasets.no.data")
	String notificationExportNoDataset();

	@Key("notification.genotype.map.no.data")
	String notificationGenotypeExportNoMap();

	@Key("notification.genotype.selection.empty")
	String notificationGenotypeExportSelectAtLeastOne();

	@Key("notification.genotype.selection.location.empty")
	String notificationGeographyNoCollsiteSelected();

	@Key("notification.climate.no.data")
	String notificationClimateNoInformationClimate();

	@Key("notification.climate.group.no.data")
	String notificationClimateNoInformationClimateGroup();

	@Key("notification.no.information")
	String notificationNoInformationFound();

	@Key("notification.map.marker.no.data")
	String notificationNoMapOrMarker();

	@Key("notification.groups.no.data")
	String notificationGroupsNoGroupsFound(String groupType);

	@Key("notification.groups.selection.empty")
	String notificationGroupsSelectAtLeastOne();

	@Key("notification.number.invalid")
	String notificationNotANumber();

	@Key("notification.phenotype.no.data")
	String notificationPhenotypeExportNoData();

	@Key("notification.page.unavailable")
	String notificationPageUnavailable();

	@Key("notification.number.range.invalid")
	String notificationNumberNotInRange(double value, double min, double max);

	@Key("notification.input.invalid")
	String notificationCheckEditTextValue();

	@Key("notification.no.data")
	String notificationNoDataFound();

	@Key("notification.groups.items.added")
	String notificationGroupItemsAdded();

	@Key("notification.groups.items.added.ignored")
	String notificationGroupItemsAddedIgnored(Integer first, Integer second);

	@Key("notification.groups.items.deleted")
	String notificationGroupItemsDeleted();

	@Key("notification.groups.deleted")
	@DefaultMessage("Groups successfully deleted.")
	String notificationGroupDeleted();

	@Key("notification.groups.empty")
	String notificationGroupsCannotBeEmpty();

	@Key("notification.groups.permissions.insufficient")
	String notificationGroupsInsufficientPermissions();

	@Key("notification.help.unavailable")
	String notificationHelpNotAvailable();

	@Key("notification.kml.error")
	String notificationKMLError();

	@Key("notification.upload.progress.no.data")
	String notificationUploadNoProgressInformation();

	@Key("notification.markers.export.options.fill.close")
	String notificationMarkersExportOptionsFillInOrClose();

	@Key("notification.internal.code.download.failed")
	String notificationCodeDownloadFailed();

	@Key("notification.internal.page.reload")
	String notificationReloadPage();

	@Key("notification.internal.client.outdated")
	String notificationClientTooOld();

	@Key("notification.cart.item.already.marked")
	String notificationCartAlreadyMarked();

	@Key("notification.library.error")
	String notificationLibraryError();

	@Key("notification.groups.upload.size.exceeded")
	String notificationGroupsUploadFileSizeExceeded();

	@Key("notification.internal.server.error.unknown")
	String notificationUnspecifiedServerError();

	@Key("notification.internal.error.unknown")
	String notificationUnknownError(String name);

	@Key("notification.internal.database.error.unknown")
	String notificationDatabaseError(String name);

	@Key("notification.internal.io.error.unknown")
	String notificationIOError();

	@Key("notification.system.read.only")
	@DefaultMessage("Germinate is operating in read-only mode. Changes have not been applied.")
	String notificationSystemReadOnly();

	@Key("notification.exception.flapjack")
	@DefaultMessage("Flapjack could not create the required file.")
	String notificationFlapjackException();

	@Key("notification.color.picker.at.least.one")
	@DefaultMessage("At least one color is required.")
	String notificationColorPickerAtLeastOne();

	@Key("notification.color.picker.at.least.two")
	@DefaultMessage("At least two colors are required.")
	String notificationColorPickerAtLeastTwo();

	@Key("notification.error.writing.file")
	@DefaultMessage("Failed to write to file on server.")
	String notificationErrorWritingFile();

	@Key("notification.incinsistancy.count.result.size")
	@DefaultMessage("Inconsistancy between total number of items and count.")
	String notificationIncinsistancyCountResult();

	@Key("notification.no.items.marked")
	@DefaultMessage("No items are marked.")
	String notificationNoItemsMarked();

	@Key("annotations.delete.confirm")
	String annotationsDeleteConfirm(@PluralCount(DefaultRule_en.class) int itemCount);

	@Key("annotations.text")
	@DefaultMessage("")
	SafeHtml annotationsText();

	@Key("error.parameter.accession.empty")
	String errorNoParameterAccession();

	@Key("page.login.text")
	SafeHtml loginText();

	@Key("page.login.username")
	String loginUsername();

	@Key("page.login.password")
	String loginPassword();

	@Key("page.login.title")
	String loginTitle();

	@Key("page.login.help")
	SafeHtml loginHelp(String gatekeeperUrl);

	@Key("page.accessions.browse.title")
	String browseAccessionsTitle();

	@Key("page.accessions.browse.text")
	SafeHtml browseAccessionsText();

	@Key("page.accessions.browse.download.text")
	SafeHtml browseAccessionsDownloadText();

	@Key("page.about.project.title")
	@DefaultMessage("About this project")
	String aboutProjectTitle();

	@Key("page.about.project.text")
	@DefaultMessage("")
	SafeHtml aboutProjectText();

	@Key("page.about.title")
	String aboutTitle();

	@Key("page.about.text")
	@DefaultMessage("page.about.text=<p><strong>&copy; 2006-{1} Information & Computational Sciences, The James Hutton Institute.</strong></p><p><a target=\"_blank\" href=\"http://ics.hutton.ac.uk/germinate\">Germinate 3</a> is written, designed and developed by <strong>Paul Shaw and Sebastian Raubach</strong></p><p>Iain Milne, Gordon Stephen and David Marshall are involved in various aspects of its development such as integration of Germinate with other <a href=\"https://ics.hutton.ac.uk/software/\">Software Development Group visualization tools</a>.</p><p>In addition the following people have been instrumental in the development of Germinate. Linda Milne, Runxuan Zhang, Bill Thomas, Luke Ramsay, Robbie Waugh, Jordi Comadran, Joanne Russell and Andy Flavell.</p><p>Previous Members of the Germinate Team have included: Jennifer Lee, Jacek Grzebyta, Toby Philp and Nelo Onyiah.</p><p>We take privacy seriously. Your username is available to the Germinate 3 system but passwords are encrypted using the BCrypt algorithm and therefore cannot be viewed by us. If you forget your password please log in to the <a href=\"{0}\" target=\"_blank\">Germinate Gatekeeper website</a> to change it.</p><p>The Germinate system knows who you are based on your username. Groups you create using Germinate are unique to you and are not available to other users of the system. Adding notes to individual plant lines are available to other users and are tagged with your username. While this is important in gaining information please do not enter anything in notes fields on this site that you want to be kept private.</p><p>This particular implementation of Germinate 3 does not actively track your IP address or your operating system but it may detect your web browser. This is done solely for the purpose of tailoring features that may not be compatible with all browsers and is integral to the GWT (GWT Web Toolkit) tools that are used to develop this resource.</p><br/><p>For further information on the tools and databases that we produce visit our website at <a href=\"http://ics.hutton.ac.uk\" target=\"_blank\">http://ics.hutton.ac.uk</a> and follow our updates on Twitter <a href=\"https://twitter.com/cropgeeks\" target=\"_blank\">@cropgeeks</a></p><br/><h4>Contact Details:</h4><p>You can contact us by email at <a href=\"mailto:germinate@hutton.ac.uk\">germinate@hutton.ac.uk</a> or you can write to us at:</p>")
	String aboutText(String gatekeeperUrl, int year);

	@Key("page.about.address")
	String aboutAddress();

	@Key("page.about.button.add.group")
	@DefaultMessage("Add group")
	String groupsButtonAddGroup();

	@Key("page.about.button.upload.members")
	@DefaultMessage("Upload")
	String groupsButtonUploadMembers();

	@Key("page.about.button.delete.members")
	@DefaultMessage("Delete")
	String groupsButtonDeleteMembers();

	@Key("page.home.title")
	String homeTitle();

	@Key("page.home.text")
	SafeHtml homeText();

	@Key("help.home")
	@DefaultMessage("<p>This is the main page of Germinate. It shows general information about this instance as well as an overview of the number of database items for certain types at the top of the page. Recent news about both the Germinate interface and the contained data are available in the news section shown in the bottom left. Finally, a section about other projects that are related to the project you are currently looking at are available in the bottom right.</p>")
	SafeHtml homeHelp();

	@Key("page.help.title")
	String helpTitle();

	@Key("widget.pager.number.format")
	String pagerNumberFormat();

	@Key("widget.pager.template.of")
	String pagerOf();

	@Key("widget.pager.template.of.over")
	String pagerOfOver();

	@Key("widget.pager.items.per.page")
	@DefaultMessage("Items per page")
	String pagerItemsPerPage();

	@Key("page.passport.passport.title")
	String passportPassportData();

	@Key("page.passport.pedigree.title")
	@DefaultMessage("Pedigree")
	String passportPedigreeData();

	@Key("page.passport.pedigree.chart")
	@DefaultMessage("Pedigree local view")
	String passportPedigreeChart();

	@Key("page.passport.pedigree.chart.sub")
	@DefaultMessage("Shows grandparents, parents and children only. Red edges represent female parents and blue edges male parents.")
	String passportPedigreeChartSub();

	@Key("page.passport.entity.title")
	@DefaultMessage("Entity data")
	String passportEntityData();

	@Key("page.passport.institutions.title")
	String passportInstitutionsData();

	@Key("page.passport.location.title")
	String passportLocationData();

	@Key("page.passport.images.title")
	String passportImages();

	@Key("page.passport.external.links.title")
	String passportExternalLinks();

	@Key("page.passport.text")
	@DefaultMessage("")
	SafeHtml passportText();

	@Key("page.passport.attributes.title")
	String passportAttributes();

	@Key("page.passport.groups.title")
	String passportGroupsOverview();

	@Key("page.passport.groups.text")
	SafeHtml passportGroupsOverviewText();

	@Key("page.passport.datasets.title")
	String passportDatasetsOverview();

	@Key("page.passport.datasets.text")
	SafeHtml passportDatasetsOverviewText();

	@Key("page.passport.pdci.title")
	@DefaultMessage("Passport Data Completeness Index")
	String passportPDCITitle();

	@Key("page.passport.pdci.score")
	@DefaultMessage("<b>The score for this item is: {0}/10</b>")
	SafeHtml passportPDCIScore(String score);

	@Key("page.passport.pdci.explanation")
	@DefaultMessage("<p>This passport data completeness index (PDCI) uses the presence or absence of data points in the documentation of a genebank accession, taking into account the presence or value of other data points. For example, a wild accession should have a well-defined collection site but no variety name. Any type of accession, wild, landrace, breeding material or modern variety, can attain a maximal score of ten for this index.</p><p>Theo van Hintum, Frank Menting and Elisabeth van Strien (2011). <b>Quality indicators for passport data in ex situ genebanks.</b> Plant Genetic Resources, 9, pp 478-485. doi: <a href='https://dx.doi.org/10.1017/S1479262111000682' target='_blank'>10.1017/S1479262111000682</a></p>")
	SafeHtml passportPDCIExplanation();

	@Key("column.passport.collection.date")
	String passportColumnColldate();

	@Key("column.passport.pdci")
	@DefaultMessage("PDCI")
	String passportColumnPDCI();

	@Key("column.passport.country")
	String passportColumnCountry();

	@Key("column.passport.genus")
	String passportColumnGenus();

	@Key("column.passport.species")
	String passportColumnSpecies();

	@Key("column.passport.subtaxa")
	@DefaultMessage("Subtaxa")
	String passportColumnSubtaxa();

	@Key("column.passport.created.on")
	String passportColumnCreatedOn();

	@Key("column.passport.gid")
	String passportColumnGID();

	@Key("column.passport.updated.on")
	String passportColumnUpdatedOn();

	@Key("column.passport.attribute.name")
	String passportColumnAttributeName();

	@Key("column.passport.attribute.description")
	String passportColumnAttributeDescription();

	@Key("column.passport.attribute.type")
	String passportColumnAttributeType();

	@Key("column.passport.attribute.value")
	String passportColumnAttributeValue();

	@Key("column.megaenvironment.name")
	String megaEnvColumnName();

	@Key("column.megaenvironment.id")
	@DefaultMessage("Id")
	String megaEnvColumnId();

	@Key("column.megaenvironment.size")
	@DefaultMessage("Size")
	String megaEnvColumnSize();

	@Key("help.passport")
	SafeHtml passportHelp();

	@Key("search.prompt")
	String searchPrompt();

	@Key("column.accessions.id")
	String accessionsColumnId();

	@Key("column.accessions.gid")
	String accessionsColumnGeneralIdentifier();

	@Key("column.accessions.name")
	String accessionsColumnName();

	@Key("column.accessions.number")
	String accessionsColumnNumber();

	@Key("column.accessions.entity.type")
	@DefaultMessage("Entity type")
	String accessionsColumnEntityType();

	@Key("column.accessions.synonym")
	@DefaultMessage("Synonyms")
	String accessionsColumnSynonym();

	@Key("column.accessions.collector.number")
	String accessionsColumnCollNumber();

	@Key("page.accessions.browse.download.column.identifier")
	String accessionsDownloadIdColumn();

	@Key("page.accessions.browse.download.group")
	String accessionsDownloadGroups();

	@Key("page.accessions.browse.download.selected.accessions")
	@DefaultMessage("Marked accessions")
	String accessionsDownloadSelectedAccessions();

	@Key("page.accessions.browse.download.attributes.include")
	@DefaultMessage("Include attributes?")
	String accessionsDownloadIncludeAttributes();

	@Key("page.accessions.browse.download.accession.data")
	@DefaultMessage("Download accession data")
	String accessionsDownloadAccessionData();

	@Key("page.accessions.browse.download.pedigree.data")
	@DefaultMessage("Download pedigree data")
	String accessionsDownloadPedigreeData();

	@Key("help.accessions.browse")
	SafeHtml accessionHelp();

	@Key("column.dataset.id")
	String datasetsColumnDatasetId();

	@Key("column.dataset.experiment.type")
	String datasetsColumnExperimentType();

	@Key("column.dataset.experiment.name")
	String datasetsColumnExperimentName();

	@Key("column.dataset.name")
	@DefaultMessage("Dataset Name")
	String datasetsColumnDatasetName();

	@Key("column.dataset.description")
	String datasetsColumnDatasetDescription();

	@Key("column.dataset.datatype")
	@DefaultMessage("Data type")
	String datasetsColumnDatasetDatatype();

	@Key("column.dataset.site.name")
	@DefaultMessage("Location")
	String datasetsColumnSiteName();

	@Key("column.dataset.country")
	@DefaultMessage("Country")
	String datasetsColumnCountry();

	@Key("column.license.description")
	@DefaultMessage("License description")
	String datasetsColumnLicenseDescription();

	@Key("column.dataset.contact")
	String datasetsColumnContact();

	@Key("column.dataset.date")
	String datasetsColumnDatasetDate();

	@Key("column.dateset.date.end")
	@DefaultMessage("End date")
	String datasetsColumnDatasetDateEnd();

	@Key("column.dataset.size")
	String datasetsColumnDatasetSize();

	@Key("column.dataset.datapoints")
	@DefaultMessage("Data points")
	String datasetsColumnDatasetDataPoints();

	@Key("column.compound.id")
	@DefaultMessage("Id")
	String compoundColumnId();

	@Key("column.compound.name")
	@DefaultMessage("Name")
	String compoundColumnName();

	@Key("column.compound.description")
	@DefaultMessage("Description")
	String compoundColumnDescription();

	@Key("column.compound.molecular.formula")
	@DefaultMessage("Molecular Formula")
	String compoundColumnMolecularFormula();

	@Key("column.compound.monoisotonic.mass")
	@DefaultMessage("Monoisotonic Mass")
	String compoundColumnMonoisotonicMass();

	@Key("column.compound.average.mass")
	@DefaultMessage("Average Mass")
	String compoundColumnAverageMass();

	@Key("column.compound.class")
	@DefaultMessage("Class")
	String compoundColumnClass();

	@Key("column.compound.unit.name")
	@DefaultMessage("Unit")
	String compoundColumnUnitName();

	@Key("column.compound.data.id")
	@DefaultMessage("Id")
	String compoundDataColumnId();

	@Key("column.compound.data.analysis.method")
	@DefaultMessage("Analysis Method")
	String compoundDataAnalysisMethod();

	@Key("column.compound.data.unit.name")
	@DefaultMessage("Unit")
	String compoundDataUnitName();

	@Key("column.compound.data.value")
	@DefaultMessage("Value")
	String compoundDataColumnValue();

	@Key("help.dataset")
	SafeHtml datasetsHelp();

	@Key("help.dataset.overview")
	@DefaultMessage("<p>This page shows all the datasets that are visible to you within this instance of Germinate. They are split into internal and external datasets. Internal datasets can be visualized and exported from Germinate whereas we only link external datasets. Each table supports sorting and filtering. Selecting a dataset will take you to the specific page for this dataset.</p>")
	SafeHtml datasetOverviewHelp();

	@Key("page.dataset.internal.title")
	String datasetsTitleInternal();

	@Key("page.dataset.external.title")
	String datasetsTitleExternal();

	@Key("page.dataset.internal.text")
	SafeHtml datasetsTextInternal();

	@Key("page.dataset.external.text")
	SafeHtml datasetsTextExternal();

	@Key("dataset.state.hidden")
	String datasetStateHidden();

	@Key("dataset.state.public")
	String datasetStatePublic();

	@Key("dataset.state.private")
	String datasetStatePrivate();

	@Key("page.genotype.groups.accessions.title")
	String genotypeExportHeadingAccessionGroups();

	@Key("page.genotype.groups.accessions.text")
	SafeHtml genotypeExportSubtitleAccessionGroups();

	@Key("page.genotype.groups.markers.title")
	String genotypeExportHeadingMarkerGroups();

	@Key("page.genotype.groups.markers.text")
	SafeHtml genotypeExportSubtitleMarkerGroups();

	@Key("page.genotype.cdf.title")
	String genotypeExportHeadingCDF();

	@Key("page.genotype.cdf.text.one")
	String genotypeExportSubtitleCDFOne();

	@Key("page.genotype.cdf.text.two")
	String genotypeExportSubtitleCDFTwo();

	@Key("page.genotype.cdf.text.three")
	String genotypeExportSubtitleCDFThree();

	@Key("page.genotype.cdf.missing.title")
	String genotypeExportHeadingMissingFilter();

	@Key("page.genotype.cdf.missing.text")
	String genotypeExportSubtitleMissingFilter();

	@Key("page.genotype.map.title")
	String genotypeExportHeadingMap();

	@Key("page.geography.locations.heatmap.title")
	String geographyCollsiteHeadingHeatmap();

	@Key("page.geography.locations.heatmap.text")
	String geographyCollsiteTextHeatmap();

	@Key("page.geography.locations.clustered.title")
	String geographyCollsiteHeadingClustered();

	@Key("page.geography.locations.table.title")
	@DefaultMessage("Location Table")
	String geographyLocationHeadingTable();

	@Key("page.geography.locations.clustered.text")
	SafeHtml geographyCollsiteTextClustered();

	@Key("page.geography.locations.synchronize")
	String geographySynchronizeMaps();

	@Key("page.geography.location.title")
	String geographyCollsiteTitle();


	@Key("page.genotype.result.title")
	String genotypeResultTitleResult();

	@Key("page.genotype.result.download.txt")
	String genotypeResultDownloadRaw();

	@Key("page.genotype.result.download.map")
	@DefaultMessage("Download map file")
	String genotypeResultDownloadMap();

	@Key("page.genotype.result.download.flapjack")
	String genotypeResultDownloadFlapjack();

	@Key("page.genotype.result.no.data")
	SafeHtml genotypeResultNoData();

	@Key("page.genotype.result.flapjack.title")
	String genotypeResultFlapjackTitle();

	@Key("page.genotype.result.flapjack.text")
	SafeHtml genotypeResultFlapjack();

	@Key("page.genotype.result.deleted.markers")
	String genotypeResultDeletedMarkers();

	@Key("page.geography.title")
	String geographyTitle(String collsite);

	@Key("page.climate.title")
	String climateTitle();

	@Key("page.climate.climate.title")
	@DefaultMessage("Select climate")
	String climateSelectClimate();

	@Key("page.climate.group.title")
	@DefaultMessage("Select group")
	String climateSelectGroup();

	@Key("page.climate.table.title")
	String climateHeadingTable();

	@Key("page.climate.map.title")
	String climateHeadingMapCollsites();

	@Key("page.groups.title")
	@DefaultMessage("Groups")
	String groupsTitle();

	@Key("page.groups.members.title")
	@DefaultMessage("Group members")
	String groupMembersTitle();

	@Key("page.groups.groups.new.text")
	String groupsSubtitleNewGroup();

	@Key("page.groups.type.select")
	String groupsSelectType();

	@Key("page.groups.public")
	String groupsGroupPublic();

	@Key("help.groups")
	@DefaultMessage("<h3>Groups</h3><p>In Germinate we define the concept of a group to be an arbitrary grouping of database items of a certain type. Germinate supports groups of <i>accessions</i>, <i>markers</i> and <i>locations</i>. These groups can be pre-created by an administrator or user-defined, which means that you can create your own groups (assuming user authentication is enabled).</p><p>The purpose of these groups becomes clear once you start exporting data. All types of data can either be exported for the whole dataset or the data can be subset into smaller chunks by selecting a single or a selection of groups. The exported data will then contain information about the selected groups only.</p><h4>Creating a group</h4><p><i>This section is only applicable if the Germinate instance you are using has user authentication enabled.</i></p><p>In addition to using the predefined groups, you can create new groups of your own. This page shows you all the existing groups in a table and upon selection, shows you its group members. New groups can be added and existing ones deleted by pressing the buttons below the groups table. Deleting a group requires you to select the checkbox in the corresponding table row as well as to have sufficient permissions to do so. When creating a new group you will be asked to select the group type and to decide on a name for the group. When you do so, the group will be associated with your user account.</p><p>Once this is done, the group will be created and Germinate will automatically select it and show the group members table (empty at this point) below the groups table. You can now manipulate the group itself by adding and removing members using the buttons below the table.</p><h4>Adding group members</h4><p>Adding members to an existing group can be achieved in two ways. You can upload a list of those items from a text file or your clipboard and Germinate will look these items up based on their identifier. Once found they will be added to the group. The other option is to use a boolean search feature that is similar to the way the table filtering works. You can choose fields from the database tables and specify values that the items in questions should equal, smaller or larger to.</p><p>Groups can be made public so that other users have the option to use them as well. If you decide to make your group public, toggle the switch button below the group members heading.</p>")
	SafeHtml groupsHelp();

	@Key("page.groups.upload.input")
	String groupsUserInput();

	@Key("page.groups.upload.file")
	String groupsUserUpload();

	@Key("page.groups.upload.tab.file")
	String groupsUserTabUpload();

	@Key("page.groups.upload.tab.input")
	String groupsUserTabCopyPaste();

	@Key("column.groups.id")
	String groupsColumnId();

	@Key("column.groups.name")
	String groupsColumnName();

	@Key("column.groups.description")
	String groupsColumnDescription();

	@Key("column.groups.type")
	@DefaultMessage("Type")
	String groupsColumnType();

	@Key("column.groups.size")
	@DefaultMessage("Size")
	String groupsColumnSize();

	@Key("column.groups.created.on")
	String groupsColumnCreatedOn();

	@Key("column.locations.id")
	String collectingsiteColumnId();

	@Key("column.locations.region")
	String collectingsiteRegion();

	@Key("column.locations.state")
	String collectingsiteState();

	@Key("column.location.type")
	@DefaultMessage("Type")
	String locationColumnType();

	@Key("column.locations.site.name")
	@DefaultMessage("Site name")
	String collectingsiteCollsite();

	@Key("column.locations.elevation")
	String collectingsiteElevation();

	@Key("column.locations.latitude")
	String collectingsiteLatitude();

	@Key("column.locations.longitude")
	String collectingsiteLongitude();

	@Key("column.locations.type.name")
	@DefaultMessage("Location type")
	String collectingsiteType();

	@Key("column.locations.distance")
	String collectingsiteDistance();

	@Key("page.megaenvironment.title")
	String megaEnvTitle();

	@Key("page.megaenvironment.table.title")
	String megaEnvHeadingTable(String megaEnvName);

	@Key("page.megaenvironment.unknown")
	String megaEnvUnknown();

	@Key("operators.equal")
	String operatorsEqual();

	@Key("operators.like")
	@DefaultMessage("Like")
	String operatorsLike();

	@Key("operators.greater.than")
	String operatorsGreaterThan();

	@Key("operators.greater.than.equals")
	@DefaultMessage("Greater than or equals")
	String operatorsGreaterThanEquals();

	@Key("operators.less.than")
	String operatorsLessThan();

	@Key("operators.less.than.equals")
	@DefaultMessage("Less than or equals")
	String operatorsLessThanEquals();

	@Key("operators.between")
	@DefaultMessage("Between")
	String operatorsBetween();

	@Key("operator.in.set")
	@DefaultMessage("In set")
	String operatorInSet();

	@Key("operators.and")
	String operatorsAnd();

	@Key("operators.or")
	String operatorsOr();

	@Key("page.phenotypes.matrix.select.group.phenotype")
	@DefaultMessage("Please select at least one phenotype and at least one group.")
	String phenotypeMatrixSelectGroupAndPhenotype();

	@Key("page.phenotypes.matrix.select.at.most")
	@DefaultMessage("Please select at most {0,number} phenotypes.")
	@AlternateMessage({"one", "Please select at most 1 phenotype."})
	String phenotypeMatrixAtMost(@PluralCount(DefaultRule_en.class) int number);

	@Key("page.compounds.matrix.select.at.most")
	@DefaultMessage("Please select at most {0,number} compounds.")
	@AlternateMessage({"one", "Please select at most 1 compound."})
	String compoundMatrixAtMost(@PluralCount(DefaultRule_en.class) int number);

	@Key("page.phenotypes.groups.accessions.title")
	String phenotypeExportHeadingAccessionGroups();

	@Key("page.phenotypes.groups.accessions.text")
	SafeHtml phenotypeExportSubtitleAccessionGroups();

	@Key("page.phenotypes.phenotypes.title")
	String phenotypeExportHeadingPhenotypes();

	@Key("page.compounds.compounds.title")
	@DefaultMessage("Select compounds")
	String compoundExportHeadingCompounds();

	@Key("page.phenotypes.phenotypes.text")
	SafeHtml phenotypeExportSubtitlePhenotypes();

	@Key("page.phenotypes.overview.title")
	@DefaultMessage("Overview")
	String phenotypeOverview();

	@Key("news.title")
	String newsTitle();

	@Key("page.markers.datasets.title")
	String markersHeadingDatasets();

	@Key("page.markers.datasets.text")
	String markersSubtitleDatasets(String name);

	@Key("page.markers.datasets.paragraph")
	SafeHtml markersParagraphDatasets();

	@Key("column.markers.name")
	String markersColumnName();

	@Key("column.markers.synonyms")
	@DefaultMessage("Synonyms")
	String markersColumnSynonym();

	@Key("column.markers.feature.description")
	String markersColumnFeatureDescription();

	@Key("column.markers.type.description")
	@DefaultMessage("Marker Type")
	String markersColumnTypeDescription();

	@Key("column.markers.chromosome")
	String markersColumnChromosome();

	@Key("column.markers.definition.start")
	String markersColumnDefinitionStart();

	@Key("column.markers.position.start")
	String markersRegionStartPosition();

	@Key("column.markers.position.end")
	String markersRegionEndPosition();

	@Key("page.maps.heatmap.title")
	@DefaultMessage("Heatmap")
	String mapsHeatmapTitle();

	@Key("page.maps.heatmap.text")
	@DefaultMessage("<p>The diagram below shows the density of markers on each chromosome. Hovering over the diagram will show the closest marker to the mouse position.</p>")
	SafeHtml mapsHeatmapText();

	@Key("page.maps.export.options.title")
	String markersExportOptionsTitle();

	@Key("page.maps.export.options.text")
	SafeHtml markersExportOptionsText();

	@Key("page.maps.export.options.tab.regions")
	String markersExportOptionsTabRegion();

	@Key("page.maps.export.options.tab.intervals")
	String markersExportOptionsTabInterval();

	@Key("page.maps.export.options.tab.radius")
	String markersExportOptionsTabRadius();

	@Key("page.maps.export.options.tab.chromosomes")
	String markersExportOptionsTabChromosomes();

	@Key("page.maps.export.options.tab.radius.offset.left")
	String markersExportOptionsRadiusOffsetLeft();

	@Key("page.maps.export.options.tab.radius.marker")
	String markersExportOptionsRadiusMarker();

	@Key("page.maps.export.options.tab.radius.offset.right")
	String markersExportOptionsRadiusOffsetRight();

	@Key("page.maps.export.options.tab.radius.text")
	SafeHtml markersExportOptionsRadiusText();

	@Key("page.maps.export.options.tab.interval.marker.first")
	String markersExportOptionsIntervalFirstMarker();

	@Key("page.maps.export.options.tab.interval.marker.second")
	String markersExportOptionsIntervalSecondMarker();

	@Key("page.maps.export.options.tab.interval.text")
	SafeHtml markersExportOptionsIntervalText();

	@Key("page.maps.export.options.tab.region.text")
	SafeHtml markersExportOptionsRegionText();

	@Key("page.maps.export.options.tab.chromosomes.text")
	SafeHtml markersExportOptionsChromosomesText();

	@Key("page.maps.export.options.close")
	String markersExportOptionsClosePanel();

	@Key("page.maps.title")
	String mapsTitle();

	@Key("page.maps.text")
	String mapsSubtitle();

	@Key("page.maps.paragraph")
	SafeHtml mapsParagraph();

	@Key("page.maps.name")
	String mapsHeadingMarkers(String mapName);

	@Key("page.maps.markers.paragraph")
	SafeHtml mapsMarkersParagraph();

	@Key("column.maps.name")
	@DefaultMessage("Map Name")
	String mapsColumnsMapName();

	@Key("column.maps.description")
	@DefaultMessage("Map Description")
	String mapsColumnsMapDescription();

	@Key("page.maps.download.format.map.chart")
	String downloadInMapChartFormat();

	@Key("page.maps.download.format.flapjack")
	String downloadInFlapjackFormat();

	@Key("page.maps.download.format.strudel")
	String downloadInStrudelFormat();

	@Key("page.geographic.search.query")
	String geographicSearchQuery();

	@Key("page.geographic.search.title")
	String geographicSearchTitle();

	@Key("page.geographic.search.text")
	String geographicSearchText();

	@Key("page.geographic.search.paragraph")
	String geographicSearchSubtitle();

	@Key("page.geographic.search.location.title")
	String geographicSearchLocationTitle();

	@Key("page.geographic.search.location.text")
	String geographicSearchLocationText();

	@Key("page.geographic.search.result.title")
	String geographicSearchResultTitle();

	@Key("page.geographic.search.accession.title")
	@DefaultMessage("Accessions ordered by distance")
	String geographicSearchAccessionTitle();

	@Key("page.geographic.search.tab.point")
	@DefaultMessage("Point search")
	String geographicSearchTabPoint();

	@Key("page.geographic.search.tab.polygon")
	@DefaultMessage("Polygon search")
	String geographicSearchTabPolygon();

	@Key("page.location.treemap.title")
	String collsiteTreemapTitle();

	@Key("page.location.treemap.text")
	SafeHtml collsiteTreemapText();

	@Key("page.location.treemap.label.location")
	@DefaultMessage("Locations")
	String collsiteTreemapLocation();

	@Key("page.allele.frequency.flapjack.title")
	String allelefreqResultFlapjackTitle();

	@Key("page.allele.frequency.flapjack.text")
	SafeHtml allelefreqResultFlapjack();

	@Key("page.allele.frequency.histogram.equal.title")
	String allelefreqHeadingEqualWidth();

	@Key("page.allele.frequency.histogram.split.title")
	String allelefreqHeadingSplit();

	@Key("page.allele.frequency.histogram.auto.title")
	String allelefreqHeadingAutomatic();

	@Key("page.allele.frequency.histogram.equal.text")
	String allelefreqTextEqualWidth();

	@Key("page.allele.frequency.histogram.split.text")
	String allelefreqTextSplit();

	@Key("page.allele.frequency.histogram.auto.text")
	String allelefreqTextAutomatic();

	@Key("page.allele.frequency.flapjack.colors")
	String allelefreqTextFlapjackColors();

	@Key("page.allele.frequency.histogram.bins")
	String allelefreqSubHeadingNrOfBins();

	@Key("page.allele.frequency.histogram.bins.left")
	String allelefreqSubHeadingNrOfBinsLeft();

	@Key("page.allele.frequency.histogram.right")
	String allelefreqSubHeadingNrOfBinsRight();

	@Key("page.allele.frequency.histogram.split.point")
	String allelefreqSubHeadingNrOfBinsSplitPoint();

	@Key("page.allele.frequency.title")
	String allelefreqFrequency();

	@Key("page.allele.frequency.cdf.text.two")
	String allelefreqSubtitleCDFTwo();

	@Key("page.allele.frequency.download.binned")
	String allelefreqResultDownloadBinned();

	@Key("page.allele.frequency.download.map")
	String allelefreqResultDownloadMap();

	@Key("page.allele.frequency.download.flapjack")
	String allelefreqResultDownloadFlapjack();

	@Key("page.search.accessions")
	String searchAccessions();

	@Key("page.search.locations")
	String searchCollectingsites();

	@Key("page.search.group")
	@DefaultMessage("Groups")
	String searchGroup();

	@Key("page.search.markers")
	String searchMarkers();

	@Key("tour.introduction.welcome.text")
	String introductionTourMessageWelcome();

	@Key("tour.introduction.navigation.text")
	String introductionTourMessageNavigation();

	@Key("tour.introduction.news.text")
	String introductionTourMessageNews();

	@Key("tour.introduction.projects.text")
	String introductionTourMessageProjects();

	@Key("tour.introduction.language.text")
	String introductionTourMessageLanguage();

	@Key("tour.introduction.share.text")
	@DefaultMessage("Click on this to reveal the supported ways of sharing this page with your colleagues/friends/...")
	String introductionTourMessageShare();

	@Key("tour.introduction.help.text")
	String introductionTourMessageHelp();

	@Key("tour.introduction.final.text")
	String introductionTourMessageFinal();

	@Key("tour.introduction.cart.text")
	String introductionTourMessageShoppingCart();

	@Key("tour.introduction.button")
	String introductionTourButton();

	@Key("page.trials.title")
	@DefaultMessage("Trials data")
	String trialsTitle();

	@Key("page.trials.overview.select.phenotypes")
	@DefaultMessage("Select phenotypes")
	String trialsOverviewSelectPhenotypes();

	@Key("page.trials.overview.select.years")
	@DefaultMessage("Select years")
	String trialsOverviewSelectYears();

	@Key("page.trials.overview.select.type")
	@DefaultMessage("Select chart type")
	String trialsOverviewSelectType();

	@Key("page.trials.phenotype.by.phenotype.coloring.none")
	@DefaultMessage("No coloring")
	String trialsPByPColorByNone();

	@Key("page.trials.phenotype.by.phenotype.coloring.treatment")
	@DefaultMessage("Color by treatment")
	String trialsPByPColorByTreatment();

	@Key("page.trials.phenotype.by.phenotype.coloring.dataset")
	@DefaultMessage("Color by dataset")
	String trialsPByPColorByDataset();

	@Key("page.trials.phenotype.by.phenotype.coloring.year")
	@DefaultMessage("Color by year")
	String trialsPByPColorByYear();

	@Key("page.trials.phenotype.by.phenotype.title")
	String trialsPByPTitle();

	@Key("page.trials.matrix.title")
	@DefaultMessage("Matrix")
	String trialsMatrix();

	@Key("page.trials.phenotype.by.phenotype.text")
	SafeHtml trialsPByPText();

	@Key("page.trials.button.plot")
	String trialsPlot();

	@Key("page.cookies.title")
	String cookieTitle();

	@Key("page.cookies.internal.title")
	String cookieTitleInternal();

	@Key("page.cookies.internal.text")
	SafeHtml cookieTextInternal();

	@Key("page.cookies.google.analytics.title")
	String cookieTitleGoogleAnalytics();

	@Key("page.cookies.google.analytics.text")
	SafeHtml cookieTextGoogleAnalytics();

	@Key("page.cookies.popup.title")
	String cookiePopupAcceptCookies();

	@Key("page.cookies.popup.text")
	String cookiePopupMessage();

	@Key("page.cart.title")
	String cartTitle();

	@Key("page.cart.text")
	String cartText();

	@Key("page.cart.button.add")
	String cartAddToCartButton();

	@Key("page.cart.button.add.all")
	String cartAddAllToCartButton();

	@Key("page.cart.button.add.page")
	@DefaultMessage("Mark items on page")
	String cartAddPageToCartButton();

	@Key("page.cart.button.add.selected")
	@DefaultMessage("Mark selected items")
	String cartAddSelectedToCartButton();

	@Key("page.cart.button.remove")
	String cartRemoveFromCartButton();

	@Key("page.cart.button.remove.all")
	String cartRemoveAllFromCartButton();

	@Key("page.cart.button.remove.page")
	@DefaultMessage("Unmark items on page")
	String cartRemovePageToCartButton();

	@Key("page.cart.button.remove.selected")
	@DefaultMessage("Unmark selected items")
	String cartRemoveSelectedFromCartButton();

	@Key("page.cart.button.clear")
	String cartEmpty();

	@Key("page.cart.button.group.create")
	String cartCreateGroup();

	@Key("page.cart.tooltip.mark")
	String cartTooltipClickToMarkItem();

	@Key("page.cart.tooltip.unmark")
	String cartTooltipClickToUnmarkItem();

	@Key("upload.file.size.limit")
	String uploadFileSizeLimit(String mb);

	@Key("page.registration.title")
	String registrationTitle();

	@Key("page.registration.username")
	String registrationUsername();

	@Key("page.registration.passport")
	String registrationPassword();

	@Key("page.registration.passport.confirm")
	String registrationConfirmPassword();

	@Key("page.registration.name.full")
	String registrationFullName();

	@Key("page.registration.email")
	String registrationEmailAddress();

	@Key("page.registration.institition.title")
	String registrationInstitution();

	@Key("page.registration.institition.name")
	String registrationInstitutionName();

	@Key("page.registration.institition.acronym")
	String registrationInstitutionAcronym();

	@Key("page.registration.institition.address")
	String registrationInstitutionAddress();

	@Key("page.registration.institition.add")
	String registrationAddNewInstitution();

	@Key("page.registration.disclaimer")
	String registrationDisclaimer();

	@Key("page.registration.disclaimer.full")
	String registrationDisclaimerFull();

	@Key("page.registration.account.exists")
	String registrationAlreadyHaveAccount();

	@Key("page.registration.gatekeeper.info")
	@DefaultMessage("Gatekeeper is Germinate''s user management system.")
	String registrationGatekeeperInfo();

	@Key("notification.registration.successful")
	String notificationRegistrationSuccess();

	@Key("notification.registration.username.exists")
	String notificationRegistrationUsernameExists();

	@Key("notification.registration.unavailable")
	String notificationRegistrationUnavailable();

	@Key("notification.registration.fields.empty")
	String notificationRegistrationFillFields();

	@Key("notification.registration.passwords.no.match")
	String notificationRegistrationPasswordsDontMatch();

	@Key("notification.registration.password.weak")
	@DefaultMessage("Your password is too weak.")
	String notificationRegistrationPasswordsWeakPassword();

	@Key("notification.registration.gatekeeper.unavailable")
	String notificationRegistrationGatekeeperUnavailable();

	@Key("notification.registration.email.failed")
	String notificationRegistrationEmailFailed();

	@Key("notification.registration.data.invalid")
	String notificationRegistrationInvalidData();

	@Key("notification.registration.account.exists")
	String notificationRegistrationAlreadyHasAccess();

	@Key("notification.registration.access.requested")
	String notificationRegistrationAlreadyRequestedAccess();

	@Key("d3.download.button.image")
	SafeHtml d3DownloadImageButton();

	@Key("d3.download.button.svg")
	SafeHtml d3DownloadSvgButton();

	@Key("d3.download.button.file")
	@DefaultMessage("Save data file")
	SafeHtml d3DownloadFileButton();

	@Key("d3.alert.select.filename")
	@DefaultMessage("Please select a filename")
	String d3AlertSelectFilename();

	@Key("page.institutions.title")
	String institutionsTitle();

	@Key("page.institutions.text")
	SafeHtml institutionsText();

	@Key("page.institutions.map.title")
	String institutionsMapTitle();

	@Key("page.institutions.map.text")
	SafeHtml institutionsMapText();

	@Key("column.institutions.id")
	String institutionsColumnId();

	@Key("column.institutions.name")
	String institutionsColumnName();

	@Key("column.institutions.acronym")
	String institutionsColumnAcronym();

	@Key("column.institutions.code")
	@DefaultMessage("Code")
	String institutionsColumnCode();

	@Key("column.institutions.country")
	String institutionsColumnCountry();

	@Key("column.institutions.contact")
	String institutionsColumnContact();

	@Key("column.institutions.phone")
	String institutionsColumnPhone();

	@Key("column.institutions.email")
	String institutionsColumnEmail();

	@Key("column.institutions.address")
	String institutionsColumnAddress();

	@Key("page.maintenance.title")
	String maintenanceTitle();

	@Key("page.maintenance.text")
	String maintenanceText();

	@Key("banner.read.only")
	@DefaultMessage("Germinate is currently operating in read-only mode. Some features have been temporarily disabled.")
	String readOnlyBanner();

	@Key("widget.dataset.title.allelefreq")
	@DefaultMessage("Allele Frequency Data Export")
	String allelefreqDatasetHeader();

	@Key("widget.dataset.text.allelefreq")
	@DefaultMessage("<p>Please select a dataset for the export process.</p>")
	SafeHtml allelefreqDatasetText();

	@Key("widget.dataset.title.trials")
	@DefaultMessage("Trials Data Export")
	String trialsDatasetHeader();

	@Key("widget.dataset.text.trials")
	@DefaultMessage("<p>Please select a dataset for the export process.</p>")
	SafeHtml trialsDatasetText();

	@Key("widget.dataset.title.genotypes")
	@DefaultMessage("Genotypic Data Export")
	String genotypeDatasetHeader();

	@Key("widget.dataset.text.genotypes")
	@DefaultMessage("<p>Please select a dataset for the export process.</p>")
	SafeHtml genotypeDatasetText();

	@Key("widget.dataset.title.compounds")
	@DefaultMessage("Compound Data Export")
	String compoundDatasetHeader();

	@Key("widget.dataset.text.compound")
	@DefaultMessage("<p>Please select a dataset for the export process.</p>")
	SafeHtml compoundDatasetText();

	@Key("widget.dataset.title.climate")
	@DefaultMessage("Climate Data Export")
	String climateDatasetHeader();

	@Key("widget.dataset.text.climate")
	@DefaultMessage("<p>Please select a dataset for the export process.</p>")
	SafeHtml climateDatasetText();

	@Key("widget.dataset.metadata.download.title")
	@DefaultMessage("Download Metadata")
	String datasetMetadataDownloadTitle();

	@Key("widget.dataset.metadata.download.text")
	@DefaultMessage("Please select at least one attribute for which to export the attribute data. Then click on the download button below the list.")
	String datasetMetadataDownloadText();

	@Key("page.acknowledgements.title")
	String acknowledgementsTitle();

	@Key("page.acknowledgements.text")
	@DefaultMessage("")
	SafeHtml acknowledgementsText();

	@Key("widget.map.marker.location")
	@DefaultMessage("Location")
	String locationMapLocation();

	@Key("widget.map.marker.dataset")
	@DefaultMessage("Dataset")
	String locationMapDataset();

	@Key("download.deleted.markers")
	@DefaultMessage("Download deleted markers")
	String downloadDeletedMarkersAsTxt();

	@Key("page.group.preview.title")
	@DefaultMessage("Group preview")
	String groupsPreviewTitle();

	@Key("page.group.preview.text")
	@DefaultMessage("<p>This page shows the database objects that have been selected based on your selection in an external tool. Please review the selection.</p><p>When you''re happy with the result, select a name for the group in the text box and hit the button to create the group.</p>")
	SafeHtml groupPreviewText();

	@Key("page.group.preview.add.button")
	@DefaultMessage("Add group")
	String groupsPreviewAddButton();


	@Key("notification.group.preview.invalid.filename")
	@DefaultMessage("Session invalidated because expected and found group members differ.")
	String notificationGroupPreviewInvalidFilename();


	@Key("column.phenotype.id")
	@DefaultMessage("Id")
	String phenotypeColumnId();

	@Key("column.phenotype.name")
	@DefaultMessage("Name")
	String phenotypeColumnName();

	@Key("column.phenotype.short.name")
	@DefaultMessage("Short name")
	String phenotypeColumnShortName();

	@Key("column.phenotype.description")
	@DefaultMessage("Description")
	String phenotypeColumnDescription();

	@Key("column.phenotype.unit.name")
	@DefaultMessage("Unit")
	String phenotypeColumnUnitName();

	@Key("column.phenotype.value")
	@DefaultMessage("Value")
	String phenotypeColumnValue();

	@Key("column.phenotype.recording.date")
	@DefaultMessage("Recording date")
	String phenotypeColumnRecordingDate();

	@Key("column.unit.name")
	@DefaultMessage("Unit")
	String unitColumnName();


	@Key("page.admin.config.insufficient.permissions")
	@DefaultMessage("You don''t have sufficient permissions to view this page and apply changes.")
	String adminConfigInsufficientPermissions();

	@Key("notification.admin.config.changes.applied")
	@DefaultMessage("Changes have been applied successfully. Refresh the page to see changes in action.")
	String notificationAdminConfigChangesApplied();


	@Key("page.admin.settings.gatekeeper.heading")
	@DefaultMessage("Gatekeeper settings")
	String adminConfigHeadingGatekeeper();

	@Key("page.admin.settings.social.heading")
	@DefaultMessage("Social media settings")
	String adminConfigHeadingSocial();

	@Key("page.admin.settings.usage.tracking.heading")
	@DefaultMessage("Usage tracking")
	String adminConfigHeadingUsageTracking();

	@Key("page.admin.settings.template.heading")
	@DefaultMessage("Theme template settings")
	String adminConfigHeadingTheme();

	@Key("page.admin.advanced.heading")
	@DefaultMessage("Advanced settings")
	String adminConfigHeadingAdvanced();

	@Key("page.admin.warning")
	@DefaultMessage("These settings will majorly influence the way Germinate works. Please only change them if you know what you are doing!")
	String adminConfigWarning();

	@Key("page.admin.button.save.changes")
	@DefaultMessage("Save changes")
	String adminConfigButtonSave();

	@Key("page.admin.alert.confirm.changes")
	@DefaultMessage("Are you sure you want to save the changes?")
	String adminConfigAlertConfirmChanges();


	@Key("notification.accessions.export.mark.at.least.one")
	@DefaultMessage("Please mark at least one accession.")
	String notificationAccessionExportMarkAtLeastOne();

	@Key("widget.contact.title")
	@DefaultMessage("Contact us")
	String contact();


	@Key("widget.table.filter.title")
	@DefaultMessage("Toggle filtering")
	String filterButtonTitle();

	@Key("widget.table.column.selector.title")
	@DefaultMessage("Select columns")
	String columnSelectorButtonTitle();

	@Key("page.geographic.search.polygon.text")
	@DefaultMessage("<p>The map below allows you to select a region by drawing a polygon around it. Once you''re happy with the selection, hit the \"Continue\" button to get the collecting sites within this polygon.</p><p>The polygon can later be edited by using the controls in the top right corner of the map. You can adjust each corner point of the polygon by dragging it. It''s also possible to add new corner points in between two existing points by dragging the semi-transparent squares.</p><p>Select the delete tool from the top right and then click on the polygon to remove it from the map.</p>")
	SafeHtml geographicSearchPolygonText();

	@Key("page.osterei.message")
	@DefaultMessage("This isn''t the easter egg you''re looking for... Try again.")
	String ostereiMessage();


	@Key("page.data.statistics.title")
	@DefaultMessage("Data Statistics")
	String dataStatisticsTitle();

	@Key("page.data.statistics.taxonomy.title")
	@DefaultMessage("Accessions grouped by taxonomy")
	String dataStatisticsTaxonomyTitle();

	@Key("page.data.statistics.taxonomy.text")
	@DefaultMessage("This pie chart visualizes the percentage of accessions for each taxonomy. Each slice represents a taxonomy. Hovering over a slice will show the actual number of accessions. Clicking on a slice will take you to the accession overview page which will then just show the accessions with this taxonomy.")
	String dataStatisticsTaxonomyText();

	@Key("page.data.statistics.accessions.per.country.title")
	@DefaultMessage("Accessions per country")
	String dataStatisticsAccessionsPerCountryTitle();

	@Key("page.data.statistics.accessions.per.country.text")
	@DefaultMessage("This chart shows the distribution of accessions. For each country the number of accessions collected in this country is color-coded. Clicking on a country will take you to the accession overview page which will then just show the accessions from this country.")
	String dataStatisticsAccessionsPerCountryText();

	@Key("page.data.statistics.datasets.title")
	@DefaultMessage("Data points per experiment type")
	String dataStatisticsDatasetsTitle();

	@Key("page.data.statistics.datasets.text")
	@DefaultMessage("This bar chart shows the number of data points per year for each of the experiment types. Each individual bar per group represent a year.")
	String dataStatisticsDatasetsText();

	@Key("page.search.section.all")
	@DefaultMessage("All data")
	String searchSectionAll();

	@Key("page.search.section.accession.data")
	@DefaultMessage("Accession data")
	String searchSectionAccessionData();

	@Key("page.search.section.accession.attribute.data")
	@DefaultMessage("Accession attribute data")
	String searchSectionAccessionAttributeData();

	@Key("page.search.section.dataset.attribute.data")
	@DefaultMessage("Dataset attribute data")
	String searchSectionDatasetAttributeData();

	@Key("page.search.section.phenotype.data")
	@DefaultMessage("Phenotype data")
	String searchSectionPhenotypeData();

	@Key("page.search.section.compound.data")
	@DefaultMessage("Compound data")
	String searchSectionCompoundData();

	@Key("page.search.section.mapdefinition.data")
	@DefaultMessage("Map definition data")
	String searchSectionMapDefinitionData();

	@Key("page.search.section.datasets")
	@DefaultMessage("Datasets")
	String searchSectionDatasets();

	@Key("page.search.section.pedigree.data")
	@DefaultMessage("Pedigree data")
	String searchSectionPedigreeData();

	@Key("page.search.section.location.data")
	@DefaultMessage("Location data")
	String searchSectionLocationData();

	@Key("page.marker.details.maps.title")
	@DefaultMessage("Maps")
	String markersMapsTitle();

	@Key("page.marker.details.maps.text")
	@DefaultMessage("This marker appears on the following maps")
	String markersMapsText();

	@Key("column.pedigree.child.gid")
	@DefaultMessage("Child GID")
	String pedigreeColumnsChildGID();

	@Key("column.pedigree.child.name")
	@DefaultMessage("Child name")
	String pedigreeColumnsChildName();

	@Key("column.pedigree.parent.gid")
	@DefaultMessage("Parent GID")
	String pedigreeColumnsParentGID();

	@Key("column.pedigree.parent.name")
	@DefaultMessage("Parent name")
	String pedigreeColumnsParentName();

	@Key("column.pedigree.relationship.type")
	@DefaultMessage("Relationship type")
	String pedigreeColumnsRelationshipType();

	@Key("column.pedigree.relationship.description")
	@DefaultMessage("Relationship description")
	String pedigreeColumnsRelationshipDescription();

	@Key("column.pedigree.pedigree.description")
	@DefaultMessage("Pedigree description")
	String pedigreeColumnsPedigreeDescription();

	@Key("column.pedigree.pedigree.author")
	@DefaultMessage("Pedigree author")
	String pedigreeColumnsPedigreeAuthor();

	@Key("search.box.tooltip")
	@DefaultMessage("Use ''%'' as the wildcard character, e.g. ''%KINGDOM%''.")
	String searchBoxTooltip();

	@Key("search.box.tooltip.in.set")
	@DefaultMessage("Separate items with commas.")
	String searchBoxTooltipInSet();

	@Key("widget.synonyms.title")
	@DefaultMessage("Synonyms")
	String synonymsTitle();

	@Key("page.compounds.title")
	@DefaultMessage("Compounds")
	String compoundsTitle();

	@Key("page.traits.title")
	@DefaultMessage("Traits")
	String traitsTitle();

	@Key("notification.polygon.search.no.polygon.selected")
	@DefaultMessage("No polygon selected.")
	String notificationNoPolygonSelected();

	@Key("page.compound.data.compound.by.compound")
	@DefaultMessage("Compound by compound")
	String compoundDataCompoundByCompound();

	@Key("page.compound.data.compound.matrix")
	@DefaultMessage("Compound matrix")
	String compoundDataCompoundMatrix();

	@Key("page.compound.details.for.compound")
	@DefaultMessage("Details for compound: {0}")
	String compoundDetailsFor(String compoundName);

	@Key("page.trait.details.for.trait")
	@DefaultMessage("Details for trait: {0}")
	String traitDetailsFor(String traitName);

	@Key("page.compound.details.image.title")
	@DefaultMessage("Images")
	String compoundDetailsImageTitle();

	@Key("page.compound.details.dataset.title")
	@DefaultMessage("Dataset")
	String compoundDetailsDatasetTitle();

	@Key("page.compound.details.dataset.text")
	@DefaultMessage("This table shows all datasets containing the selected compound. Additionally, the number of germplasm for which there is a value for this compound within the current dataset is shown alongside the total number of compound values per dataset.")
	String compoundDetailsDatasetText();

	@Key("page.trait.details.image.title")
	@DefaultMessage("Images")
	String traitDetailsImageTitle();

	@Key("page.trait.details.datasets.title")
	@DefaultMessage("Datasets")
	String traitDetailsDatasetTitle();

	@Key("page.trait.details.dataset.text")
	@DefaultMessage("This table shows all datasets containing the selected trait. Additionally, the number of germplasm for which there is a value for this trait within the current dataset is shown alongside the total number of phenotype values per dataset.")
	String traitDetailsDatasetText();

	@Key("mcpd.accename")
	@DefaultMessage("Accession name")
	String mcpdAccename();

	@Key("mcpd.puid")
	@DefaultMessage("Persistent unique identifier")
	String mcpdPuid();

	@Key("mcpd.instcode")
	@DefaultMessage("Institute code")
	String mcpdInstcode();

	@Key("mcpd.accenumb")
	@DefaultMessage("Accession number")
	String mcpdAccenumb();

	@Key("mcpd.collnumb")
	@DefaultMessage("Collecting number")
	String mcpdCollnumb();

	@Key("mcpd.collcode")
	@DefaultMessage("Collecting institute code")
	String mcpdCollcode();

	@Key("mcpd.collname")
	@DefaultMessage("Collecting institute name")
	String mcpdCollname();

	@Key("mcpd.collinstaddress")
	@DefaultMessage("Collecting institute address")
	String mcpdCollinstaddress();

	@Key("mcpd.collmissid")
	@DefaultMessage("Collecting mission identifier")
	String mcpdCollmissid();

	@Key("mcpd.genus")
	@DefaultMessage("Genus")
	String mcpdGenus();

	@Key("mcpd.species")
	@DefaultMessage("Species")
	String mcpdSpecies();

	@Key("mcpd.spauthor")
	@DefaultMessage("Species authority")
	String mcpdSpauthor();

	@Key("mcpd.subtaxa")
	@DefaultMessage("Subtaxon")
	String mcpdSubtaxa();

	@Key("mcpd.subtauthor")
	@DefaultMessage("Subtaxon authority")
	String mcpdSubtauthor();

	@Key("mcpd.cropname")
	@DefaultMessage("Common crop name")
	String mcpdCropname();

	@Key("mcpd.acqdate")
	@DefaultMessage("Acquisition date")
	String mcpdAcqdate();

	@Key("mcpd.origcty")
	@DefaultMessage("Country of origin")
	String mcpdOrigcty();

	@Key("mcpd.coorduncert")
	@DefaultMessage("Coorinate uncertainty [m]")
	String mcpdCoorduncert();

	@Key("mcpd.coorddatum")
	@DefaultMessage("Coordinate datum")
	String mcpdCoorddatum();

	@Key("mcpd.georefmeth")
	@DefaultMessage("Georeferencing method")
	String mcpdGeorefmeth();

	@Key("mcpd.colldate")
	@DefaultMessage("Collecting date of sample")
	String mcpdColldate();

	@Key("mcpd.bredcode")
	@DefaultMessage("Breeding institute code")
	String mcpdBredcode();

	@Key("mcpd.bredname")
	@DefaultMessage("Breeding institute name")
	String mcpdBredname();

	@Key("mcpd.sampstat")
	@DefaultMessage("Biological status of accession")
	String mcpdSampstat();

	@Key("mcpd.collsre")
	@DefaultMessage("Collecting/acquisition source")
	String mcpdCollsrc();

	@Key("mcpd.donorcode")
	@DefaultMessage("Donor institute code")
	String mcpdDonorcode();

	@Key("mcpd.donorname")
	@DefaultMessage("Donor institute name")
	String mcpdDonorname();

	@Key("mcpd.donornumb")
	@DefaultMessage("Donor accession number")
	String mcpdDonornumb();

	@Key("mcpd.othernumb")
	@DefaultMessage("Other identifiers associated with this accession")
	String mcpdOthernumb();

	@Key("mcpd.duplsite")
	@DefaultMessage("Location of safety duplicates")
	String mcpdDuplsite();

	@Key("mcpd.duplinstname")
	@DefaultMessage("Institute maintaining safety duplicates")
	String mcpdDuplinstname();

	@Key("mcpd.storage")
	@DefaultMessage("Type of germplasm storage")
	String mcpdStorage();

	@Key("mcpd.mlsstat")
	@DefaultMessage("MLS status of the accession")
	String mcpdMlsstat();

	@Key("mcpd.remarks")
	@DefaultMessage("Remarks")
	String mcpdRemarks();

	@Key("button.group.from.cart")
	@DefaultMessage("Create group from list")
	String buttonCreateGroupFromCart();

	@Key("button.group.from.selection")
	@DefaultMessage("Create group from selection")
	String buttonCreateGroupFromSelection();

	@Key("page.shopping.cart.title")
	@DefaultMessage("Marked items")
	String shoppingCartPageTitle();

	@Key("page.search.data.category")
	@DefaultMessage("Select what data you want to search for")
	String searchPageDataCategory();

	@Key("page.search.query.label")
	@DefaultMessage("Please enter the search query")
	String searchPageSearchBox();

	@Key("page.search.results.title")
	@DefaultMessage("Results")
	String searchTitleResults();

	@Key("page.locations.select.type")
	@DefaultMessage("Select location type")
	String locationTypesSelect();

	@Key("page.genotypes.title")
	@DefaultMessage("Genotype data")
	String genotypePageTitle();

	@Key("page.allelefreq.title")
	@DefaultMessage("Allele frequency data")
	String allelefreqPageTitle();

	@Key("page.allelefreq.binning.title")
	@DefaultMessage("Allele Frequency Binning")
	String allelefreqBinningTitle();

	@Key("page.compounds.scatter.first.compound")
	@DefaultMessage("First compound")
	String compoundFirstCompound();

	@Key("page.compounds.scatter.second.compound")
	@DefaultMessage("Second compound")
	String compoundSecondCompound();

	@Key("page.phenotypes.scatter.first.phenotype")
	@DefaultMessage("First phenotype")
	String phenotypeFirstPhenotype();

	@Key("page.phenotypes.scatter.second.phenotype")
	@DefaultMessage("Second phenotype")
	String phenotypeSecondPhenotype();

	@Key("page.compounds.scatter.group")
	@DefaultMessage("Group")
	String compoundGroup();

	@Key("page.compounds.scatter.color")
	@DefaultMessage("Color by")
	String compoundColor();

	@Key("page.admin.config.menu.item")
	@DefaultMessage("Admin settings")
	String adminConfigMenuItem();

	@Key("column.comment.id")
	@DefaultMessage("Id")
	String commentColumnId();

	@Key("column.comment.description")
	@DefaultMessage("Description")
	String commentColumnDescription();

	@Key("column.comment.type.description")
	@DefaultMessage("Comment type")
	String commentColumnType();

	@Key("column.comment.created.on")
	@DefaultMessage("Created on")
	String commentColumnCreatedOn();

	@Key("column.comment.user")
	@DefaultMessage("User")
	String commentColumnUser();

	@Key("widget.comment.type.select")
	@DefaultMessage("Select comment type")
	String commentTypeSelect();

	@Key("widget.comment.content")
	@DefaultMessage("Enter comment message")
	String commentContent();

	@Key("notification.action.permissions.insufficient")
	@DefaultMessage("Insufficient permissions to perform action.")
	String notificationActionInsufficientPermissions();

	@Key("page.login.registration.link")
	@DefaultMessage("Don''t have an account? Register here!")
	String loginRegistrationLink();

	@Key("page.login.password.reset.link")
	@DefaultMessage("Forgot your password?")
	String loginForgotPasswordLink();

	@Key("widget.wizard.progress.page")
	@DefaultMessage("Page {0}/{1}")
	String wizardProgress(int page, int outOf);

	@Key("page.gallery.select.image.type")
	@DefaultMessage("Select image type")
	String gallerySelectImageType();

	@Key("page.gallery.type.all.images")
	@DefaultMessage("All images")
	String galleryAllImageFiles();

	@Key("template.footer.copyright")
	@DefaultMessage("© Information & Computational Sciences, JHI 2005-{0}")
	String copyright(String currentYear);

	@Key("help.admin.config")
	@DefaultMessage("<p>This page let''s you configure Germinate the way you want it to be. Please be aware that changes to this page will majorly affect how Germinate works and looks, so be careful.</p>")
	SafeHtml adminConfigHelp();

	@Key("page.marker.title")
	@DefaultMessage("Marker details")
	String markersTitle();

	@Key("page.groups.delete.group.title")
	@DefaultMessage("Delete groups")
	String groupsDeleteTitle();

	@Key("page.groups.delete.group.text")
	@DefaultMessage("Do you really want to delete the selected groups?")
	String groupsDeleteText();

	@Key("page.groups.delete.members.title")
	@DefaultMessage("Delete group members")
	String groupMembersDeleteTitle();

	@Key("page.groups.delete.members.text")
	@DefaultMessage("Do you really want to delete the selected group members?")
	String groupMembersDeleteText();

	@Key("widget.pager.jump.to.page.title")
	@DefaultMessage("Jump to page")
	String pagerJumpToPageTitle();

	@Key("widget.pager.jump.to.page.page.number")
	@DefaultMessage("Page number (1 - {0})")
	String pagerPageNumberInput(int maxPage);

	@Key("page.groups.search.result")
	@DefaultMessage("Search for items")
	String groupsNewMembersTitle();

	@Key("page.groups.search.result.subtitle")
	@DefaultMessage("Use the table filter or just browse the table")
	String groupsNewMembersSubtitle();

	@Key("widget.table.filter.info")
	@DefaultMessage("<b>Table filtering supports many different comparisons. ''Equal'' searches for exact matches, while 'Like', by default, looks for matches starting or ending with the query, but also support the wildcard character ''%''.</b>")
	SafeHtml tableFilterInfo();

	@Key("wizard.license.title")
	@DefaultMessage("License")
	String licenseWizardTitle();

	@Key("wizard.license.agreement.decline.meaning")
	@DefaultMessage("Declining a license means that you won''t be able to see and export the contained data.")
	String licenseWizardDeclineMeaning();

	@Key("page.search.additional.datasets.title")
	@DefaultMessage("Additional data")
	String searchAdditionalDatasetsTitle();

	@Key("page.search.additional.datasets.text")
	@DefaultMessage("<p>There is data in additional datasets for which you haven''t accepted the license yet. If you wish to do so, please check the datasets you are interested in and then click the button below the table.</p><p>The licenses of the selected datasets will then be shown and you can decide for each one if you would like to accept it or not. Afterwards, the view will update.</p>")
	SafeHtml searchAdditionalDatasetsText();

	@Key("page.search.additional.datasets.text.short")
	@DefaultMessage("<p>There is data in additional datasets for which you haven''t accepted the license yet. If you wish to do so, please check the table at the bottom of the page.</p>")
	SafeHtml searchAdditionalDatasetsTextShort();

	@Key("page.dataset.attributes.title")
	@DefaultMessage("Attributes")
	String datasetAttributesTitle();

	@Key("page.datasets.collaborators.title")
	@DefaultMessage("Collaborators")
	String datasetCollaboratorsTitle();

	@Key("page.experiment.details.title")
	@DefaultMessage("Experiment details")
	String experimentDetailsTitle();

	@Key("page.experiment.details.text")
	@DefaultMessage("<p>This page shows all the datasets that are part of the selected experiment.</p>")
	SafeHtml experimentDetailsText();

	@Key("widget.list.groups.all.markers")
	@DefaultMessage("All markers")
	String groupsAllMarkers();

	@Key("widget.list.groups.all.accessions")
	@DefaultMessage("All accessions")
	String groupsAllAccessions();

	@Key("widget.list.groups.all.locations")
	@DefaultMessage("All locations")
	String groupsAllLocations();

	@Key("help.search")
	@DefaultMessage("Germinate supports full-text search across various data types and columns within this type. You can search by typing a search query into the text box. Upon search, Germinate shows the search results page. This page shows all matching database objects grouped into categories, each category representing a different data type. The number of matching items is shown for each section on the right. Upon expanding of a section, a result table will show the matching database items. You can then either download the data, mark specific items or filter down further by adjusting the search criteria in the table header.")
	String searchHelp();

	@Key("help.marked.items")
	@DefaultMessage("<p>Another useful feature of Germinate is the concept of <i>marked item lists</i>. A marked item is either an accession, a marker or a location that is of interest to the user. While you are browsing the page, a lot of the tables will have a checkbox column as the last column which you can use to mark certain items. Germinate will keep track of these items for you.</p><p>To see how many items you currently have marked, you can click on the menu item in the top bar or go directly to the marked item lists page.</p><p>Once you have marked all the items that you are interested in, you can create a group of these items and use them to export data against them. To create a group, you can either go to the marked item lists page or by clicking on the header of the checkbox column and selecting \"Create group from selection\".</p>")
	SafeHtml markedItemsHelp();

	@Key("page.registration.disclaimer.short.html")
	@DefaultMessage("")
	SafeHtml registrationDisclaimerShortHtml();

	@Key("page.registration.disclaimer.long.html")
	@DefaultMessage("")
	SafeHtml registrationDisclaimerLongHtml();

	@Key("widget.dublin.core.page.header")
	@DefaultMessage("Dublin Core")
	String dublinCoreHeader();

	@Key("widget.dublin.core.title")
	@DefaultMessage("Title")
	String dublinCoreTitle();

	@Key("widget.dublin.core.subject")
	@DefaultMessage("Subject")
	String dublinCoreSubject();

	@Key("widget.dublin.core.description")
	@DefaultMessage("Description")
	String dublinCoreDescription();

	@Key("widget.dublin.core.type")
	@DefaultMessage("Type")
	String dublinCoreType();

	@Key("widget.dublin.core.source")
	@DefaultMessage("Source")
	String dublinCoreSource();

	@Key("widget.dublin.core.relation")
	@DefaultMessage("Relation")
	String dublinCoreRelation();

	@Key("widget.dublin.core.coverage")
	@DefaultMessage("Coverage")
	String dublinCoreCoverage();

	@Key("widget.dublin.core.creator")
	@DefaultMessage("Creator")
	String dublinCoreCreator();

	@Key("widget.dublin.core.publisher")
	@DefaultMessage("Publisher")
	String dublinCorePublisher();

	@Key("widget.dublin.core.contributor")
	@DefaultMessage("Contributor")
	String dublinCoreContributor();

	@Key("widget.dublin.core.rights")
	@DefaultMessage("Rights")
	String dublinCoreRights();

	@Key("widget.dublin.core.date")
	@DefaultMessage("Date")
	String dublinCoreDate();

	@Key("widget.dublin.core.format")
	@DefaultMessage("Format")
	String dublinCoreFormat();

	@Key("widget.dublin.core.identifier")
	@DefaultMessage("Identifier")
	String dublinCoreIdentifier();

	@Key("widget.dublin.core.language")
	@DefaultMessage("Language")
	String dublinCoreLanguage();

	@Key("column.collaborator.id")
	@DefaultMessage("Id")
	String collaboratorColumnId();

	@Key("column.collaborator.first.name")
	@DefaultMessage("First name")
	String collaboratorColumnFirstName();

	@Key("column.collaborator.last.name")
	@DefaultMessage("Last name")
	String collaboratorColumnLastName();

	@Key("column.collaborator.email")
	@DefaultMessage("Email")
	String collaboratorColumnEmail();

	@Key("column.collaborator.phone")
	@DefaultMessage("Phone number")
	String collaboratorColumnPhone();

	@Key("widget.group.member.upload.select.column")
	@DefaultMessage("Select database column")
	String groupMemberUploadSelectColumn();

	@Key("widget.marked.item.list.clear.confirm")
	@DefaultMessage("Are you sure you want to clear the marked item list?")
	String markedItemListClearConfirm();

	@Key("page.about.buttons.homepage.title")
	@DefaultMessage("Visit the Germinate homepage")
	String aboutButtonsHomepageTitle();

	@Key("page.about.buttons.homepage.url")
	@DefaultMessage("https://ics.hutton.ac.uk/get-germinate")
	String aboutButtonsHomepageUrl();

	@Key("page.about.buttons.github.title")
	@DefaultMessage("View Germinate''s source code on GitHub")
	String aboutButtonsGithubTitle();

	@Key("page.about.buttons.github.url")
	@DefaultMessage("https://github.com/germinateplatform/germinate")
	String aboutButtonsGithubUrl();

	@Key("page.about.buttons.publication.title")
	@DefaultMessage("View or cite the Germinate publication")
	String aboutButtonsPublicationTitle();

	@Key("page.about.buttons.publication.url")
	@DefaultMessage("https://dl.sciencesocieties.org/publications/cs/articles/57/3/1259")
	String aboutButtonsPublicationUrl();

	@Key("page.about.buttons.documentation.title")
	@DefaultMessage("Read the Germinate documentation")
	String aboutButtonsDocumentationTitle();

	@Key("page.about.buttons.documentation.url")
	@DefaultMessage("https://github.com/germinateplatform/germinate/wiki")
	String aboutButtonsDocumentationUrl();

	@Key("widget.user.tracking.heading")
	@DefaultMessage("")
	SafeHtml userTrackingHeading();

	@Key("widget.dataset.selected")
	@DefaultMessage("Selected datasets")
	String selectedDatasets();

	@Key("widget.dataset.user.tracking.name")
	@DefaultMessage("Full name")
	String userTrackingName();

	@Key("widget.dataset.user.tracking.email")
	@DefaultMessage("Email address")
	String userTrackingEmail();

	@Key("widget.dataset.user.tracking.institution")
	@DefaultMessage("Institution")
	String userTrackingInstitution();

	@Key("widget.dataset.user.tracking.explanation")
	@DefaultMessage("Can you let us know what you intend to use this data for in your research?")
	String userTrackingExplanation();

	@Key("widget.dataset.user.tracking.explanation.option.basic.research")
	@DefaultMessage("Basic research (molecular biology, QTL studies, GWAS, phylogenetics, molecular ecology, etc.)")
	String userTrackingExplanationOptionBasic();

	@Key("widget.dataset.user.tracking.explanation.option.prebreeding")
	@DefaultMessage("Pre-breeding (introgression line development, germplasm evaluation etc.)")
	String userTrackingExplanationOptionPreBreeding();

	@Key("widget.dataset.user.tracking.explanation.option.breeding.cultivar")
	@DefaultMessage("Breeding and cultivar development")
	String userTrackingExplanationOptionBreedingCultivar();

	@Key("widget.dataset.user.tracking.explanation.option.education")
	@DefaultMessage("Education")
	String userTrackingExplanationOptionEducation();

	@Key("widget.dataset.user.tracking.explanation.option.direct.use")
	@DefaultMessage("Direct use of germplasm for production")
	String userTrackingExplanationOptionDirectUse();

	@Key("widget.dataset.user.tracking.explanation.option.other")
	@DefaultMessage("Other (please specify)")
	String userTrackingExplanationOptionOther();

	@Key("widget.privacy.policy.information")
	@DefaultMessage("")
	SafeHtml privacyPolicyInformation();

	@Key("widget.password.strength.zero")
	@DefaultMessage("Your password is too guessable.")
	String passwordStrengthZero();

	@Key("widget.password.strength.one")
	@DefaultMessage("Your password is very guessable.")
	String passwordStrengthOne();

	@Key("widget.password.strength.two")
	@DefaultMessage("Your password is somewhat guessable.")
	String passwordStrengthTwo();

	@Key("widget.password.strength.three")
	@DefaultMessage("Your password is safely unguessable.")
	String passwordStrengthThree();

	@Key("widget.password.strength.four")
	@DefaultMessage("Your password is very unguessable.")
	String passwordStrengthFour();


	@Key("page.user.groups.title")
	@DefaultMessage("User groups")
	String userGroupsTitle();

	@Key("page.user.groups.text")
	@DefaultMessage("<p>The table below shows the groups of users that are currently defined. You can add new groups, delete existing groups and add users to or remove users from groups.</p>")
	SafeHtml userGroupsText();

	@Key("page.user.groups.add.user")
	@DefaultMessage("Add user")
	String userGroupsAddUser();

	@Key("page.dataset.permissions.add.group")
	@DefaultMessage("Add group")
	String userGroupsAddGroup();

	@Key("page.dataset.permissions.title")
	@DefaultMessage("Dataset permissions")
	String datasetPermissionsTitle();

	@Key("page.dataset.permissions.text")
	@DefaultMessage("<p>The table below shows all available datasets. After selecting a dataset, you can define which user groups or individual users should have access to this dataset.</p>")
	String datasetPermissionsText();

	@Key("column.user.username")
	@DefaultMessage("Username")
	String userColumnUsername();

	@Key("column.user.fullname")
	@DefaultMessage("Full name")
	String userColumnFullName();

	@Key("column.user.email")
	@DefaultMessage("Email")
	String userColumnEmail();

	@Key("column.user.institution")
	@DefaultMessage("Institution")
	String userColumnInstitution();

	@Key("page.dataset.permissions.dataset")
	@DefaultMessage("Dataset")
	String datasetPermissionsDataset();

	@Key("page.groups.current.members")
	@DefaultMessage("Current group members")
	String groupMembersCurrent();

	@Key("page.dataset.permissions.users.title")
	@DefaultMessage("User permissions")
	String datasetPermissionsUsersTitle();

	@Key("page.dataset.permissions.groups.title")
	@DefaultMessage("Group permissions")
	String datasetPermissionsGroupsTitle();

	@Key("page.dataset.permissions.current.user.permissions")
	@DefaultMessage("Current user permissions")
	String datasetPermissionsCurrentUserPermissions();

	@Key("page.dataset.permissions.current.group.permissions")
	@DefaultMessage("Current group permissions")
	String datasetPermissionsCurrentGroupPermissions();

	@Key("widget.dataset.download.attributes")
	@DefaultMessage("Attributes")
	String datasetAttributesDownloadAttributes();

	@Key("widget.dataset.download.dublin.core")
	@DefaultMessage("Dublin Core")
	String datasetAttributesDownloadDublinCore();

	@Key("page.trialsites.title")
	@DefaultMessage("Trial Site Details")
	String trialSitesTitle();
}