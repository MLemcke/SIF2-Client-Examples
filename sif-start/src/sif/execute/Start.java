package sif.execute;
import java.io.File;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXParseException;

import sif.IO.xml.SifMarshaller;
import sif.frontOffice.FrontDesk;
import sif.model.policy.DynamicPolicy;


/**
 * @author Manuel Lemcke
 *
 */
public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String requestName = "Request 1";
		String reportPath = "reports";
		String errorMessageLine1 = "Not all arguments were provided. Expected:";
		String errorMessageLine2 = "policyPath spreadsheetPath requestName reportPath(without starting /)";
		switch (args.length){
		case 0:
		case 1:
			System.out.println(errorMessageLine1);
			System.out.println(errorMessageLine2);
			return;
		case 3:
			System.out.println(errorMessageLine1);
			System.out.println(errorMessageLine2);
			System.out.println("The report path ist set to: " + reportPath);	
			System.out.println("The request's name ist set to: " + requestName);
			break;
		case 2:
			System.out.println(errorMessageLine1);
			System.out.println(errorMessageLine2);
			System.out.println("The request's name ist set to: " + requestName);
			break;
		}
		
		FrontDesk desk = FrontDesk.getInstance();
		
		System.out.println("Opening policy: " + args[0]);
		File policyFile = new File(args[0]);
		File spreadsheetFile = new File(args[1]);

		try {
			DynamicPolicy policy = SifMarshaller.unmarshal(policyFile);
			System.out.println("Created policy.");
//			String filepath = policy.getSpreadsheetFileName();
//			System.out.println("Found spreadsheet reference: " + filepath);
			System.out.println("Opening spreadsheet: " + spreadsheetFile.getAbsolutePath());
			desk.createAndRunDynamicInspectionRequest(args[2], spreadsheetFile, policy);	
			System.out.println("Writing report " + args[2] + ".html.");
			desk.createInspectionReport(args[3]);
		} catch (SAXParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
