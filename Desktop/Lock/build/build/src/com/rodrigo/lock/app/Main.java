package com.rodrigo.lock.app;
	
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.rodrigo.lock.app.Utils.LenguajeUtils;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	
	@FXML
    private VBox vBox;
	@FXML
	AnchorPane drophere;
	@FXML
	Label textDrophere;
	
	ResourceBundle bundle  = LenguajeUtils.getBundle();
	/*
    private Set<String> stringSet = new HashSet();;
    ObservableList observableList = FXCollections.observableArrayList();
	*/
    private BorderPane rootLayout;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("RootLayout.fxml"));
	        loader.setController(this);
	        rootLayout = (BorderPane) loader.load();
	      
            Scene scene = new Scene(rootLayout);
            
            textDrophere.setText(bundle.getString("drop"));
            
            scene.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    if (db.hasFiles()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    } else {
                        event.consume();
                    }
                }
            });
            
            // Dropping over surface
            scene.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasFiles()) {
                        success = true;
                        String filePath = null;
                        List<File> archivos = new LinkedList<>();
                        		
                        for (File file:db.getFiles()) {
                            //filePath = file.getAbsolutePath();
                            //System.out.println(filePath);                    		
                            drophere.managedProperty().bind(drophere.visibleProperty());
                            drophere.setVisible(false);                       	
                            
                            archivos.add(file);
                        }
                        resolverAccion(archivos);

                    }
                    event.setDropCompleted(success);
                    event.consume();
                }


            });
            
            
            
            
			scene.getStylesheets().add("file:resources/css/application.css");
            primaryStage.setScene(scene);
            primaryStage.show();	

            
            initArgs();
            
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void initArgs(){
		List<File> l = new LinkedList<>();
		for (String s : args) {
            File f = new File(s);
            if (f.exists()) {            	
            	l.add(f);
            }
        }
		
		if (!l.isEmpty()){
			drophere.managedProperty().bind(drophere.visibleProperty());
            drophere.setVisible(false); 
			resolverAccion(l);
		}
	}
	

	
	
	
	
	
	
	
	private void resolverAccion(List<File> archivos) {
		 try {
			 	if (archivos!= null && !archivos.isEmpty()){
	                vBox.getChildren().add((new EncryptCard(archivos)).getBasePane());
			 	}
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
		
	}
	


	
	
    public void FileNotFound(String uri){
       /* String error;
        if (uri != null) {
            error = String.format(getResources().getString(R.string.error_notfound2), uri.toString());
        }else{
            error = getResources().getString(R.string.error_nofind);
        }

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_noblock))
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
*/

    }

	
	
	
	
	
	
    static String args[];
	
	public static void main(String[] args) {
		Main.args = args;
		
		launch(args);		
		
		
	}
}
