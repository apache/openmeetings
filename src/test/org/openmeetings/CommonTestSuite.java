package org.openmeetings;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.batik.TestSVGExporter;
import org.openmeetings.batik.TestSVGTextExporter;
import org.openmeetings.batik.TestSimpleSVGDom;
import org.openmeetings.calendar.*;
import org.openmeetings.chat.TestChatLinking;
import org.openmeetings.emotes.ConvertGifs;
import org.openmeetings.emotes.TestChatParsing;
import org.openmeetings.jai.TestInterpolation;
import org.openmeetings.jai.TestSVGTextExporterArrow;
import org.openmeetings.rdc.TestKeyCodesNumber;
import org.openmeetings.rdc.TestReadKeyCodesNumber;
import org.openmeetings.rss.TestRssLoader;
import org.openmeetings.server.*;
import org.openmeetings.sip.DebugConnection;
import org.openmeetings.sip.TestRPCGateway;
import org.openmeetings.test.adresses.TestAddEmailToAdress;
import org.openmeetings.test.adresses.TestAdresses;
import org.openmeetings.test.adresses.TestgetAdress;
import org.openmeetings.test.basic.StartUpData;
import org.openmeetings.test.basic.StartUpLanguageFieldsConference;
import org.openmeetings.test.dao.DaoTestSuite;
import org.openmeetings.test.domain.TestAddGroup;
import org.openmeetings.test.domain.TestUserGroupAggregation;
import org.openmeetings.test.gui.TestGui;
import org.openmeetings.test.init.*;
import org.openmeetings.test.library.TestFileParser;
import org.openmeetings.test.navi.GenerateBasicNavi;
import org.openmeetings.test.navi.TestNavi;
import org.openmeetings.test.record.BatchConversion;
import org.openmeetings.test.record.GetRecordings;
import org.openmeetings.test.rooms.AddOrgRoom;
import org.openmeetings.test.rooms.AddRoomTypes;
import org.openmeetings.test.rooms.GetRoomByOrganisations;
import org.openmeetings.test.rooms.RoomTest;
import org.openmeetings.test.userdata.*;
import org.openmeetings.timezone.GetIds;
import org.openmeetings.timezone.GetOffset;
import org.openmeetings.timezone.TestIds;
import org.openmeetings.utils.DefaultSchemeCreator;
import org.openmeetings.utils.TestReflectionApi;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class CommonTestSuite {

        private static final Logger log = Red5LoggerFactory.getLogger(CommonTestSuite.class);

    private static void setUp() {
        if (ScopeApplicationAdapter.webAppPath.length() == 0){
			String webAppPath = System.getProperty("webAppPath");
			ScopeApplicationAdapter.webAppPath = webAppPath;
		}
        try {
            if (Usermanagement.getInstance().getUserById(1L) == null) {
                DefaultSchemeCreator.makeDefaultScheme();
                log.info("Default scheme created successfully");
            } else {
                log.info("Default scheme already created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Common test suite");
        setUp();

        addBatikTests(suite);
        addCalendarTests(suite);
        addChatTests(suite);
        addEmotesTests(suite);
        addJaiTests(suite);
        addRdcTests(suite);
        addRssTests(suite);
        addServerTests(suite);
        addSipTests(suite);
        addAdressesTests(suite);
        addBasicTests(suite);
        addDaoTests(suite);
        addDomainTests(suite);
        addGuiTests(suite);
        addInitTests(suite);

        addLibraryTests(suite);
        addNaviTests(suite);
        addRecordTests(suite);
        addRoomsTests(suite);
        addUserDataTests(suite);
        addTimezoneTests(suite);
        addUtilsTests(suite);

        return suite;
    }

    private static void addBatikTests(TestSuite suite) {
        suite.addTestSuite(TestSVGExporter.class);
        suite.addTestSuite(TestSVGTextExporter.class);
        suite.addTestSuite(TestSimpleSVGDom.class);
    }

    private static void addCalendarTests(TestSuite suite) {
        suite.addTestSuite(TestDatabaseStructureAppointment.class);
        suite.addTestSuite(TestDatabaseStructureAppointmentReminderTyp.class);
        suite.addTestSuite(TestDatabaseStructureCategory.class);
        suite.addTestSuite(TestDatabaseStructureGetAppointmentByRange.class);
        suite.addTestSuite(TestDatabaseStructureGetUserStart.class);
        suite.addTestSuite(TestDatabaseStructureMeetingMember.class);
        suite.addTestSuite(TestDatabaseStructureUsersSearch.class);
    }

    private static void addChatTests(TestSuite suite) {
        suite.addTestSuite(TestChatLinking.class);
    }

    private static void addEmotesTests(TestSuite suite) {
        suite.addTestSuite(ConvertGifs.class);
        suite.addTestSuite(TestChatParsing.class);
    }

    private static void addJaiTests(TestSuite suite) {
        suite.addTestSuite(TestInterpolation.class);
        suite.addTestSuite(TestSVGTextExporterArrow.class);
    }

    private static void addRdcTests(TestSuite suite) {
        suite.addTestSuite(TestKeyCodesNumber.class);
        suite.addTestSuite(TestReadKeyCodesNumber.class);
    }

    private static void addRssTests(TestSuite suite) {
        suite.addTestSuite(TestRssLoader.class);
    }

    private static void addServerTests(TestSuite suite) {
        suite.addTestSuite(TestGZipDeltaPackage.class);
        suite.addTestSuite(TestGZipPackage.class);
        suite.addTestSuite(TestJPEGZipPackage.class);
        suite.addTestSuite(TestPNGZipPackage.class);
        suite.addTestSuite(TestScrollbar.class);
        suite.addTestSuite(TestSender.class);
        suite.addTestSuite(TestSocket.class);
        suite.addTestSuite(TestWebStartGui.class);
        suite.addTestSuite(TestWebStartViewerGui.class);
    }

    private static void addSipTests(TestSuite suite) {
        suite.addTestSuite(DebugConnection.class);
        suite.addTestSuite(TestRPCGateway.class);
    }

    private static void addAdressesTests(TestSuite suite) {
        suite.addTestSuite(TestAddEmailToAdress.class);
        suite.addTestSuite(TestAdresses.class);
        suite.addTestSuite(TestgetAdress.class);
    }

    private static void addBasicTests(TestSuite suite) {
        suite.addTestSuite(StartUpData.class);
        suite.addTestSuite(StartUpLanguageFieldsConference.class);
    }

    private static void addDaoTests(TestSuite suite) {
        suite.addTest(DaoTestSuite.suite());
    }

    private static void addDomainTests(TestSuite suite) {
        suite.addTestSuite(TestAddGroup.class);
        suite.addTestSuite(TestUserGroupAggregation.class);
    }

    private static void addGuiTests(TestSuite suite) {
        suite.addTestSuite(TestGui.class);
    }

    private static void addInitTests(TestSuite suite) {
        suite.addTestSuite(AddDefaultConfigItems.class);
        suite.addTestSuite(AddDefaultfield.class);
        suite.addTestSuite(AddTitles.class);
        suite.addTestSuite(AddUserLevels.class);
        suite.addTestSuite(Addadminuser.class);
        suite.addTestSuite(AdddefaultLanugages.class);
        suite.addTestSuite(getDefaultFields.class);
    }

    private static void addLibraryTests(TestSuite suite) {
        suite.addTestSuite(TestFileParser.class);
    }

    private static void addNaviTests(TestSuite suite) {
        suite.addTestSuite(GenerateBasicNavi.class);
        suite.addTestSuite(TestNavi.class);
    }

    private static void addRecordTests(TestSuite suite) {
        suite.addTestSuite(BatchConversion.class);
        suite.addTestSuite(GetRecordings.class);
    }

    private static void addRoomsTests(TestSuite suite) {
        suite.addTestSuite(AddOrgRoom.class);
        suite.addTestSuite(AddRoomTypes.class);
        suite.addTestSuite(GetRoomByOrganisations.class);
        suite.addTestSuite(RoomTest.class);
    }

    private static void addUserDataTests(TestSuite suite) {
        suite.addTestSuite(AddConfigParams.class);
        suite.addTestSuite(DeleteUser.class);
        suite.addTestSuite(RegisterUser.class);
        suite.addTestSuite(TestAuth.class);
        suite.addTestSuite(TestLogin.class);
        suite.addTestSuite(TestMD5.class);
        suite.addTestSuite(UpdateUserSelf.class);
        suite.addTestSuite(UserManagement.class);
    }

    private static void addTimezoneTests(TestSuite suite) {
        suite.addTestSuite(GetIds.class);
        suite.addTestSuite(GetOffset.class);
        suite.addTestSuite(TestIds.class);
    }

    private static void addUtilsTests(TestSuite suite) {
        suite.addTestSuite(TestReflectionApi.class);
    }
}
