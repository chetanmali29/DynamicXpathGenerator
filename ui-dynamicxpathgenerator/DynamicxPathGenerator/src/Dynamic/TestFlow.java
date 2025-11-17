package Dynamic;



public class TestFlow {
	Operations m_Operation = null;
	TestFlow m_TestFlow;
	Functions m_Functions; 

	public TestFlow()
	{
		m_Functions = new Functions();
	}  
	//OLD METHOD FULLY FUNCTIONAL --- 05/05/2025 In USE
	public String TRG_UI_AI_BT(String Url) {
		String intRet = "0";
		try {
			intRet = m_Functions.invokeBrowser(Url);

			if ("0".equals(intRet)) {
				intRet = m_Functions.login2();
			}
			if("0".equals(intRet))
			{
				System.out.println("---------- Login Successfull ---------");
			}
			if (intRet =="0") {
				intRet = m_Functions.smartClickHandler();
			} 
			if(intRet.contains("ERR003") || intRet.contains("ERR004"))
			{
				if(intRet.contains("ERR003"))
				{
					System.out.println("Element is no longer attached to the DOM. Skipping. -> Re-Execute the Application");
					intRet = "0";
				}else if(intRet.contains("ERR004"))
				{
					System.out.println("Session issue or timeout -> Re-Execute the Application");
					intRet = "0";
				}
			}

			//            if ("0".equals(intRet)) {
			//                intRet = m_Functions.clickOnDefectManagement();
			//            }
			//            if ("0".equals(intRet)) {
			//                intRet = m_Functions.clickOnAddDefect();
			//            }
			//            if ("0".equals(intRet)) {
			//                intRet = m_Functions.FillDefectInformation();
			//            }

		} catch (Exception e) {
			e.printStackTrace();
			intRet = "1";
		}
		return intRet;
	}
}
