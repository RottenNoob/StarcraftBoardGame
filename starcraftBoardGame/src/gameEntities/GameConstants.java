package gameEntities;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**classe contenant les constantes du jeu**/
public class GameConstants {
	/**nom de la situation où se trouvent les unités à placer au départ**/
	public static final String startingUnitSituation = "startingPool";
	/**nom de la situation où se trouve les unités qui étaient dans la galaxie au début du tour**/
	public static final String inGalaxySituation = "inGalaxy";
	/**nom du tour où l'on place une base**/
	public static final String placeBaseTurnName = "placeBase";
	/**nom du tour où l'on place les routes**/
	public static final String placeZRoadTurnName = "placeZRoad";
	/**nom du tour où l'on place les ordres**/
	public static final String planningPhaseTurnName = "planningPhase";
	/**nom du tour où l'on choisit l'ordre à exécuter**/
	public static final String galaxyOrderChoiceTurnName = "galaxyOrderChoice";
	/**nom du tour où l'on choisit si on exécute ou annule un ordre **/
	public static final String executeChoiceTurnName = "executeChoice";
	/**nom du tour où l'on choisit si on exécute un ordre de mouvement **/
	public static final String moveUnitTurnName = "moveUnit";
	/**nom du tour où l'on évacue les unités perdantes ou trop nombreuses après une bataille **/
	public static final String moveRetreatUnitTurnName = "moveRetreatUnit";
	/**nom du tour où l'on construit des unités **/
	public static final String buildUnitsTurnName = "buildUnits";
	/**nom du tour où l'on construit des batiments ou modules **/
	public static final String buildBuildingsTurnName = "buildBuildings";
	/**nom du tour où l'on construit une base **/
	public static final String buildBaseTurnName = "buildBase";
	
	
	
	/**renvoie l'image d'une unité**/
	public static String getUnitImage(String unitName){
		String result = "";
		URL resources = GameConstants.class.getClassLoader().getResource("../../starcraftResources/unitList.xml");
		try {
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/root/faction/unit[@name =\""+ unitName +"\"]/@img");
			Object o = expr.evaluate(doc, XPathConstants.STRING);
			result = (String) o;
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return result;
	}
}
