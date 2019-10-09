package com.rodrigo.lock.app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import UI.MaterialDesignButton;

import com.rodrigo.lock.app.Utils.LenguajeUtils;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.datatype.AddFileListener;
import com.rodrigo.lock.core.exceptions.LockException;
import com.rodrigo.lock.core.utils.FileUtils;
import com.rodrigo.lock.core.utils.TextUtils;

public class EncryptCard {
	@FXML
	private AnchorPane basePane;
	@FXML
	private HBox encryptPane;
	@FXML
	private ImageView image;
	@FXML
	private Label title;
	@FXML
	private Label error;
	@FXML
	private TextField fileName;
	@FXML
	private PasswordField passwordField1;
	@FXML
	private PasswordField passwordField2;
//	@FXML
//	private CheckBox checkBox;
	@FXML
	private Label subTitle;
	@FXML
	private AnchorPane marginSubTitle;
	@FXML
	private AnchorPane progressBar;

//	MaterialDesignButton materialDesignButtonPrimary;
//	MaterialDesignButton materialDesignButtonSecondary;
	ResourceBundle bundle = LenguajeUtils.getBundle();
	/*
	 * @FXML private Label label1;
	 * 
	 * @FXML private Label label2;
	 */

	// FileHeader cabezal= new FileHeader();
	// EncryptHandlerV2 controler;

//	private String nombre;
//	private String tamanio;

	private List<File> archivosIn;
	//private File contenidoDeArchivo=null;
	
	public EncryptCard(List<File> archivos) {
		this.archivosIn=archivos;
		
		// this.controler= controler;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EncryptCard.fxml"));
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//este no se usa mas
//		checkBox.managedProperty().bind(checkBox.visibleProperty());
//		checkBox.setVisible(true);

		//
		passwordField1.setPromptText(bundle.getString("password"));
		passwordField2.setPromptText(bundle.getString("repassword"));
		fileName.setPromptText(bundle.getString("vault_name"));
		

		
		///
		boolean esUnabobeda=false;
		if (archivos.size() ==1){
			esUnabobeda= FileUtils.esBobeda(archivos.get(0));
		}
		
		if (esUnabobeda){
			setInterfaceToDecrypt(false);
		}else{
			if (archivos.get(0).isDirectory()){
				fileName.setText(archivos.get(0).getName());
			}else{
				//fileName.setText(FileUtils.createNameForFile());				
			}
			
			setInterfaceToEncrypt(false);			
		}
		


	}

	private void setInterfaceToEncrypt(boolean showError) {
		//se setea texto
		String nombre;
		String tamanio;
		int count = FileUtils.countFileNodes(archivosIn);
		if (count == 1) {
			nombre = (bundle.getString("file"));
		} else {
			nombre = (count + " " + bundle.getString("files"));
		}
		long size = FileUtils.getSize(archivosIn);
		tamanio = FileUtils.sizeToString(size);

		title.setText(nombre);
		subTitle.setText(tamanio);		
		
		//se setea acciones
		
		encryptPane.getChildren().clear();

		MaterialDesignButton materialDesignButtonPrimary = new MaterialDesignButton(bundle.getString("lockm"));
		materialDesignButtonPrimary.getStyleClass().add("btn-primary");
		encryptPane.getChildren().add(materialDesignButtonPrimary);		
		materialDesignButtonPrimary.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cifrar();
			}
		});

		//se inicializa visibilidad	
		error.managedProperty().bind(error.visibleProperty());
		error.setVisible(showError);			

		fileName.managedProperty().bind(fileName.visibleProperty());
		fileName.setVisible(true);
		
		passwordField1.managedProperty().bind(passwordField1.visibleProperty());
		passwordField1.setVisible(true);

		passwordField2.managedProperty().bind(passwordField2.visibleProperty());
		passwordField2.setVisible(true);

		progressBar.managedProperty().bind(progressBar.visibleProperty());
		progressBar.setVisible(false);
		
		image.setImage(new Image("file:resources/images/lock.png"));
		
	}

	public AnchorPane getBasePane() {
		return this.basePane;
	}

	public void cifrar() {
		error.managedProperty().bind(error.visibleProperty());
		error.setVisible(false);

		fileName.managedProperty().bind(fileName.visibleProperty());
		fileName.setVisible(false);
		
		passwordField1.managedProperty().bind(passwordField1.visibleProperty());
		passwordField1.setVisible(false);

		passwordField2.managedProperty().bind(passwordField2.visibleProperty());
		passwordField2.setVisible(false);
		
		encryptPane.getChildren().clear();

		progressBar.managedProperty().bind(progressBar.visibleProperty());
		progressBar.setVisible(true);

		title.setText(bundle.getString("encrypting"));

		Task task = new Task<Void>() {
			@Override
			public Void call() {
				try {
					encrypt();
				} catch (Exception e) {
				}
				return null;
			}
		};

		new Thread(task).start();

		// materialDesignButton.setText("ABRIR");
	}


	void encrypt() {
		Boolean cancel = false;

		String fileName =this.fileName.getText().toString();
		String pass1 = this.passwordField1.getText().toString();
		String pass2 = this.passwordField2.getText().toString();

		// /if (cabezal.isCifrar()) {
		if (TextUtils.isEmpty(fileName)) {
			cancel = true;
			setErrorCifrar(bundle.getString("empty_bobeda"));
		}

		if (TextUtils.isEmpty(pass1)) {
			cancel = true;
			setErrorCifrar(bundle.getString("empty_password"));
		}

		if (TextUtils.isEmpty(pass2)) {
			cancel = true;
			setErrorCifrar(bundle.getString("re_password"));
		}

		if (!pass1.equals(pass2) && !cancel) {
			cancel = true;
			setErrorCifrar(bundle.getString("nomatch_password"));
		}

		// }
		try {
			if (!cancel) {
				String pathSalida = archivosIn.get(0).getParent();
				pathSalida = pathSalida + File.separator + fileName + "." + FileUtils.LOCK_EXTENSION;
				
				List<File> contenido = new LinkedList();
				for (File f: archivosIn){
					if (f.isDirectory()){
						for (File iter: f.listFiles()){
							contenido.add(iter);
						}
					}else{
						contenido.add(f);
					}
				}				
				
				EncryptedFileSystem controller =  EncryptedFileSystemHandler.createEncryptedFile(pathSalida, pass1);
				controller.addFile(new AddFileListener(), contenido);
				
				FileUtils.delete(archivosIn);
				
				archivosIn.clear();
				archivosIn.add(new File(pathSalida));
				
				terminadoCifrarCorrectamenteEncrypt();

			}

		} catch (LockException e) {
			String error = bundle.getString(e.getCode());
			if (e.getParams() != null) {
				error = String.format(error, e.getParams());
			}
			setErrorCifrar(error);
			e.printStackTrace();
		} catch (Exception e) {
			setErrorCifrar(bundle.getString(LockException.error_general));
			e.printStackTrace();

		}

	}

	public void setErrorCifrar(String message) {
		Platform.runLater(new Runnable() {
			public void run() {
				setInterfaceToEncrypt(true);
				error.setText(message);
				error.requestFocus();
			}
		});

	}

	public void terminadoCifrarCorrectamenteEncrypt() {
		Platform.runLater(new Runnable() {
			public void run() {
				
				encryptPane.getChildren().clear();				
				MaterialDesignButton materialDesignButtonSecondary = new MaterialDesignButton(bundle.getString("openm"));
				materialDesignButtonSecondary.getStyleClass().add("btn-default");
				encryptPane.getChildren().add(materialDesignButtonSecondary);				
				materialDesignButtonSecondary.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								try {
									setInterfaceToDecrypt(false);
									//Desktop.getDesktop().open(archivosIn.get(0));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
				materialDesignButtonSecondary.managedProperty().bind(materialDesignButtonSecondary.visibleProperty());
				materialDesignButtonSecondary.setVisible(true);
				
				progressBar.managedProperty().bind(progressBar.visibleProperty());
				progressBar.setVisible(false);

				title.setText(bundle.getString("encryptingDone"));
				subTitle.setText(archivosIn.get(0).getAbsolutePath());

				image.setImage(new Image("file:resources/images/lock.png"));
			}
		});
	}
	
	
	
	
	


	private void setInterfaceToDecrypt(boolean showError) {
		//se setea texto
		String nombre =  bundle.getString("decrypt");		
		long size = FileUtils.getSize(archivosIn);
		String tamanio = FileUtils.sizeToString(size);

		title.setText(nombre);
		subTitle.setText(tamanio);		
		
		//se setea acciones
		
		encryptPane.getChildren().clear();
		MaterialDesignButton materialDesignButtonPrimary = new MaterialDesignButton(bundle.getString("unlockm"));
		materialDesignButtonPrimary.getStyleClass().add("btn-primary");
		encryptPane.getChildren().add(materialDesignButtonPrimary);
		materialDesignButtonPrimary.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				descifrar();
			}
		});

		//se inicializa visibilidad	
		error.managedProperty().bind(error.visibleProperty());
		error.setVisible(showError);			

		fileName.managedProperty().bind(fileName.visibleProperty());
		fileName.setVisible(false);
		
		passwordField1.managedProperty().bind(passwordField1.visibleProperty());
		passwordField1.setVisible(true);

		passwordField2.managedProperty().bind(passwordField2.visibleProperty());
		passwordField2.setVisible(false);

		materialDesignButtonPrimary.managedProperty().bind(materialDesignButtonPrimary.visibleProperty());
		materialDesignButtonPrimary.setVisible(true);

		progressBar.managedProperty().bind(progressBar.visibleProperty());
		progressBar.setVisible(false);
		
		image.setImage(new Image("file:resources/images/unlock.png"));
		
	}


	

  public void descifrar(){  	
  	error.managedProperty().bind(error.visibleProperty());
  	error.setVisible(false);
  	
  	passwordField1.managedProperty().bind(passwordField1.visibleProperty());
  	passwordField1.setVisible(false);  	    	
	
	encryptPane.getChildren().clear();
  	    	
  	progressBar.managedProperty().bind(progressBar.visibleProperty());
  	progressBar.setVisible(true); 	
  	
  	title.setText(bundle.getString("decrypting"));
  	
  	Task task = new Task<Void>() {
  	    @Override public Void call() {
  	    	try{    	    		
  	    		decrypt();
  	    		   	    		
  	    	}catch (Exception e){
  	    		
  	    	}
  	        return null;
  	    }
  	};
  	    
  	    
  	new Thread(task).start();

  }
  
	
  
  void decrypt() {
  	try{
        String pass1 = passwordField1.getText().toString();
    	EncryptedFileSystem controller =  EncryptedFileSystemHandler.openEncryptedFile(archivosIn.get(0).getAbsolutePath(), pass1);
    	String outdir =FileUtils.createNewFileNameInPath(archivosIn.get(0).getParent()  + File.separator, FileUtils.removeExtensionFile(archivosIn.get(0).getName()), "");
    	File f= new File (outdir);
    	controller.extractAllFilesAndFolders(f);
    	EncryptedFileSystemHandler.removeFromUso(archivosIn.get(0).getAbsolutePath());
    	
    	FileUtils.delete(archivosIn.get(0));
    	
    	archivosIn.clear();
    	archivosIn.add(f);
    	
    	terminadoDescifrarCorrectamente();
        
         
  	} catch (LockException e) { 
      	e.printStackTrace();
  		String error = bundle.getString(e.getCode());
  		if (e.getParams() != null){
  			error = String.format(error, e.getParams());
  		}
  		setErrorDescifrar(error); 
      } catch (Exception e) {
        e.printStackTrace();
    	setErrorDescifrar(bundle.getString(LockException.error_general));
      }

  }
  
  
	public void setErrorDescifrar(String message) {
		Platform.runLater(new Runnable() {
			public void run() {
				setInterfaceToDecrypt(true);
				error.setText(message);
				error.requestFocus();
			}
		});

	}
  
  

	public void terminadoDescifrarCorrectamente() {
		Platform.runLater(new Runnable() {
			public void run() {
				
				encryptPane.getChildren().clear();

				MaterialDesignButton materialDesignButtonSecondary = new MaterialDesignButton(bundle.getString("openm"));
				materialDesignButtonSecondary.getStyleClass().add("btn-default");
				encryptPane.getChildren().add(materialDesignButtonSecondary);				
				materialDesignButtonSecondary.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								try {
									Desktop.getDesktop().open(archivosIn.get(0));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
				materialDesignButtonSecondary.managedProperty().bind(materialDesignButtonSecondary.visibleProperty());
				materialDesignButtonSecondary.setVisible(true);
				
				
				
				
				materialDesignButtonSecondary = new MaterialDesignButton(bundle.getString("lockm"));
				materialDesignButtonSecondary.getStyleClass().add("btn-default");
				encryptPane.getChildren().add(materialDesignButtonSecondary);				
				materialDesignButtonSecondary.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								try {
									fileName.setText(archivosIn.get(0).getName());
									setInterfaceToEncrypt(false);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
				materialDesignButtonSecondary.managedProperty().bind(materialDesignButtonSecondary.visibleProperty());
				materialDesignButtonSecondary.setVisible(true);
				
				
				
				
				
				
				progressBar.managedProperty().bind(progressBar.visibleProperty());
				progressBar.setVisible(false);

				title.setText(bundle.getString("decryptingDone"));
				subTitle.setText(archivosIn.get(0).getAbsolutePath());

				image.setImage(new Image("file:resources/images/unlock.png"));
			}
		});
	}
	

}