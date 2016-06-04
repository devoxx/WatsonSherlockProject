import java.io.IOException;

/**
 * @author Stephan Janssen
 */
public class DeleteDocuments {

    static String files[] = {
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/446793131",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1619130258",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1090561649",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/522636211",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1004057546",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/214500148",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/597335127",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1944291233",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2011727085",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/426645386",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/317242242",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2010224367",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/663274942",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1831429369",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/59738338",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/819672396",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1177071873",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/304070499",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/359607289",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1962524430",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1372469100",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/230985975",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1016185901",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1712112544",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1362565288",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/714488448",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1429946398",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1836414055",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2114170065",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/623158891",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/559060672",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1718746056",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1203389333",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1086835992",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/260907519",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2004061897",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1787235601",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2128535918",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1186437597",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/733520505",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/817924187",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1550794143",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/753243726",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1728326413",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/354523279",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/956214722",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1289028814",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/332306215",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/418017910",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/683319059",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/172441054",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/682988353",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1810819337",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/83431213",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/28202242",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/90580042",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1985209600",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/155464732",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1382115781",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/163032177",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/174862214",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/20895353",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1495475981",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/833715187",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1257297564",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/411888783",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/853491005",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1711540051",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2059784212",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1239793087",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2068352575",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2081250711",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/5694689",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/459314954",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/964811458",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1683694480",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/267422747",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1029070759",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1378765082",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1709436572",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/820557881",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/863647739",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/594198243",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2086525916",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1208796921",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/549233951",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/624747364",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1688700941",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/592601318",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/519147087",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2065183402",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/932802961",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2075004307",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1061542531",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1434870551",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2037684240",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/686648397",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1723129402",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/808925289",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1423341195",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/889876333",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1104819170",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1245212134",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1015326987",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/655677352",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1323455444",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1056189134",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1342125235",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1697943684",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1308377026",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1753976532",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/1411015681",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/646631400",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/38133713",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/733549696",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/638627807",
            "/corpora/eve6tionsto1/devoxx_corpus1/documents/2131857777"};


    DeleteDocuments() throws IOException {

        for (String file : files) {
            String cmd = "curl -X DELETE -u a425694b-f06a-4956-886d-e2e9e66d7c65:FPhRNXvLnJ9m https://gateway.watsonplatform.net/concept-insights/api/v2" + file;
            Process p = Runtime.getRuntime().exec(cmd);

            System.out.println(cmd);

            // wait for 10 seconds and then destroy the process
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.destroy();

        }
    }

    public static void main(String args[]) {
        try {
            new DeleteDocuments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
