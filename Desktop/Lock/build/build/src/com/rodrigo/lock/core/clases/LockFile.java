package com.rodrigo.lock.core.clases;

public class LockFile {

	private String id;
	private FileType type;
	private String fullPath;
	private Long start;
	private Long size;
	private String previewId;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public FileType getType() {
		return type;
	}
	public void setType(FileType type) {
		this.type = type;
	}


	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getPreviewId() {
		return previewId;
	}
	public void setPreviewId(String previewId) {
		this.previewId = previewId;
	}
	
	
	
}
