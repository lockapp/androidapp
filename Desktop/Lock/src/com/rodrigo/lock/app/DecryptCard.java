package com.rodrigo.lock.app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.rodrigo.lock.app.Utils.LenguajeUtils;
import com.rodrigo.lock.core.utils.FileUtils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import UI.MaterialDesignButton;



public class DecryptCard {
//    @FXML
//    private AnchorPane basePane;	
//	@FXML
//	private AnchorPane encryptPane;
//	@FXML
//	private ImageView  image;
//	@FXML
//	private Label  title;
//	@FXML
//	private Label  error;
//	@FXML
//	private  PasswordField passwordField1;
//	@FXML
//	private CheckBox  conservarOriginal;
//	@FXML
//	private Label subTitle;
//	@FXML
//	private AnchorPane marginSubTitle;
//	@FXML
//	private AnchorPane  progressBar;
//	
//    MaterialDesignButton materialDesignButton;
//    ResourceBundle bundle  = LenguajeUtils.getBundle();
//    
//    DecryptHandlerV2 controler;
//    
//    private String nombre;
//    private String tamanio;
//    
//    public DecryptCard(DecryptHandlerV2 controler) {
//    	this.controler= controler;    	
//    	
//    	//
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DecryptCard.fxml"));
//        fxmlLoader.setController(this);
//        try {
//            fxmlLoader.load();
//        } catch (IOException e)  {
//            throw new RuntimeException(e);
//        }     
//        
//        conservarOriginal.setText(bundle.getString("dejarcopia")); 
//        passwordField1.setPromptText(bundle.getString("password"));
//
//        materialDesignButton = new MaterialDesignButton(bundle.getString("unlockm"));
//        materialDesignButton.getStyleClass().add("btn-primary");        
//        materialDesignButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//            	descifrar();
//            }
//        });
//        encryptPane.getChildren().add(materialDesignButton);        
//        
//    	error.managedProperty().bind(error.visibleProperty());
//    	error.setVisible(false);
//      
//    	progressBar.managedProperty().bind(progressBar.visibleProperty());
//    	progressBar.setVisible(false); 	 	
//    	
//    	
//    	
//    	int count = FileUtils.countFileNodes(controler.getInFile());
//    	if(count == 1){
//    		nombre= (bundle.getString("file"));
//        }else{
//        	nombre = ( count + " " + bundle.getString("files"));
//        }
//    	title.setText(nombre);
//    	long size =FileUtils.getSize(controler.getInFile());
//    	tamanio=FileUtils.sizeToString(size);
//    	subTitle.setText(tamanio);
//        
//    }
//    
//    /*
//    public void setInfo(String string) {
//        label1.setText(string);
//        label2.setText(string);
//    }
//    */
//    
//    public AnchorPane getBasePane(){
//    	return this.basePane;
//    }
//
// 
//    public void descifrar(){  	
//    	error.managedProperty().bind(error.visibleProperty());
//    	error.setVisible(false);
//    	
//    	passwordField1.managedProperty().bind(passwordField1.visibleProperty());
//    	passwordField1.setVisible(false);
//    	    	
//    	conservarOriginal.managedProperty().bind(conservarOriginal.visibleProperty());
//    	conservarOriginal.setVisible(false);
//    	
//    	materialDesignButton.managedProperty().bind(materialDesignButton.visibleProperty());
//    	materialDesignButton.setVisible(false);
//    	    	
//    	progressBar.managedProperty().bind(progressBar.visibleProperty());
//    	progressBar.setVisible(true); 	
//    	
//    	title.setText(bundle.getString("decrypting"));
//   
//    	
//    	
//    	Task task = new Task<Void>() {
//    	    @Override public Void call() {
//    	    	try{    	    		
//    	    		decrypt();
//    	    		   	    		
//    	    	}catch (Exception e){
//    	    		
//    	    	}
//    	        return null;
//    	    }
//    	};
//    	    
//    	    
//    	new Thread(task).start();
//
//    	
//    
//    
//    	//materialDesignButton.setText("ABRIR");
//    }
//    
//    
//    
//    DecryptListenerImpl decryptListenerImpl;
//    DecryptOptions opciones;
//    void decrypt() {
//    	try{
//	
//	        String pass1 = passwordField1.getText().toString();
//	
//	    	opciones = new DecryptOptions();
//			opciones.setConservarOriginal(conservarOriginal.isSelected());
//			opciones.setPassword(pass1);
//			opciones.setRutaSalida(controler.getInFile().getParent() );
//			
//			controler.init(opciones);
//			decryptListenerImpl =new DecryptListenerImpl();
//			controler.decrypt(decryptListenerImpl);
//			
//
//	        terminadoCorrectamente();
//	        
//           
//    	} catch (LockException e) {
//    		String error = bundle.getString(e.getCode());
//    		if (e.getParams() != null){
//    			error = String.format(error, e.getParams());
//    		}
//        	setError(error);  
//        	e.printStackTrace();
//        } catch (Exception e) {
//        	setError(bundle.getString(LockException.unknown_error));
//        	e.printStackTrace();
//        }
//
//    }
//
//    
//    
//    
//	public void setError(String message) {		
//		Platform.runLater(new Runnable() {
//		    public void run() {				
//		    	error.managedProperty().bind(error.visibleProperty());
//		    	error.setVisible(true);
//		    	
//		    	passwordField1.managedProperty().bind(passwordField1.visibleProperty());
//		    	passwordField1.setVisible(true);
//		    			    	
//		    	conservarOriginal.managedProperty().bind(conservarOriginal.visibleProperty());
//		    	conservarOriginal.setVisible(true);
//		    	
//		    	materialDesignButton.managedProperty().bind(materialDesignButton.visibleProperty());
//		    	materialDesignButton.setVisible(true);
//		    	    	
//		    	progressBar.managedProperty().bind(progressBar.visibleProperty());
//		    	progressBar.setVisible(false); 	
//		    	
//		    	title.setText(nombre);
//		    	subTitle.setText(tamanio);
//		    	error.setText(message);
//		    	error.requestFocus();
//		    	
//		    }
//		});
//		
//	}
//
//	
//
//	public void terminadoCorrectamente(){
//		Platform.runLater(new Runnable() {
//		    public void run() {			    	
//		    	
//	    	
//	    	
//	    	   materialDesignButton = new MaterialDesignButton(bundle.getString("openm"));
//	           materialDesignButton.getStyleClass().add("btn-default");        
//	           materialDesignButton.setOnAction(new EventHandler<ActionEvent>() {
//		             @Override
//		             public void handle(ActionEvent event) {
//		            	 try {
//		            		 Desktop.getDesktop().open(new File(opciones.getRutaSalida()));
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//		             }
//		         });
//		    	
//		        encryptPane.getChildren().add(materialDesignButton);        
//
//		    	progressBar.managedProperty().bind(progressBar.visibleProperty());
//		    	progressBar.setVisible(false); 	
//		    	
//		    	title.setText(bundle.getString("decryptingDone"));
//		    	subTitle.setText(opciones.getRutaSalida());
//		    	
//		    	image.setImage(new Image("file:resources/images/unlock.png"));
//		    	
//		    }
//		});
//	}
//    
//    
//    
//  
//	
//	
//	class DecryptListenerImpl implements DecryptListener{
//
//		private int numberOfFiles =0;
//		private List<File> decryptedFiles = new LinkedList<File>();
//		
//		
//		@Override
//		public void setNumberOfFile(int i) {
//			numberOfFiles=i;
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void newFileDecrypted(File f) {
//			decryptedFiles.add(f);
//			
//		}
//
//		public int getNumberOfFiles() {
//			return numberOfFiles;
//		}
//
//		public List<File> getDecryptedFiles() {
//			return decryptedFiles;
//		}
//		
//		
//	}
//    
//    
}