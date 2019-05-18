package ece;
import javafx.application.Application ;
import javafx.fxml.FXMLLoader ;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;

public class ScratchForArduino extends  Application{
    @Override
    public void start ( Stage stage ) throws Exception {
        /*
        URL url = new URL(getClass().getResource("PapacodeModules.s2e.json").toString());
        // read from the URL
        Scanner scan = new Scanner(url.openStream());
        String str = new String();
        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();
        JSONObject json = new JSONObject(str);
        System.out.println(json.getString("author"));
        JSONArray array = json.getJSONArray("blockSpecs");
        for(Object blocks:array){
            System.out.println(((JSONArray)blocks).getString(0));
        }
        */
        Parent root =
                FXMLLoader . load ( getClass (). getResource ( "ui.fxml" ));
        Scene scene = new Scene ( root );
        stage . setTitle ( "ScratchFor1234" ); // displayed in window's title bar
        stage . setScene ( scene );
        stage . show ();
    }
    public static void main ( String [] args ) {
        launch ( args );
    }
}

