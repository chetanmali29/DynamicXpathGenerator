package Dynamic;

public class TestCase {

    TestFlow m_TestFlow = new TestFlow();
    String strEntityName = "";
    String returnCode = "";

    public void executeRTMUOB(String strOption, String Url) throws Exception {
        try {
            //System.out.println("Dynamic xPaths Generator"); // : " + strOption);
            switch (strOption.trim()) {
                case "AI - BTAPPLICATION":
                    strEntityName = "AI - BTAPPLICATION";
                    returnCode = m_TestFlow.TRG_UI_AI_BT(Url);
                    break;
                case "AI - UOBAPPLICATION":
                    strEntityName = "AI - UOBAPPLICATION";
                    returnCode = m_TestFlow.TRG_UI_AI_BT(Url);
                    break;
                default:
                    System.out.println("Invalid option: " + strOption);
            }
        } catch (Exception e) {
            System.out.println("Error in executeRTMUOB: " + e.getMessage());
        } finally {
            closeAllInstances();
        }
    }

    private void closeAllInstances() {
        //Operations.closeBrowser();
    }
}
