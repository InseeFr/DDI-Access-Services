package fr.insee.rmes.tocolecticaapi.models;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link MultipartFile} implementation for Apache Commons FileUpload.
 *
 * @author Trevor D. Cook
 * @author Juergen Hoeller
 * @since 29.09.2003
 */
@SuppressWarnings("serial")
public class CommonsMultipartFile implements MultipartFile {

    protected static final Log logger = LogFactory.getLog(CommonsMultipartFile.class);

    private final FileItem fileItem;

    private final long size;

    private boolean preserveFilename = false;


    /**
     * Create an instance wrapping the given FileItem.
     *
     * @param fileItem the FileItem to wrap
     */
    public CommonsMultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }


    /**
     * Return the underlying {@code org.apache.commons.fileupload.FileItem}
     * instance. There is hardly any need to access this.
     */
    public final FileItem getFileItem() {
        return this.fileItem;
    }

    /**
     * Set whether to preserve the filename as sent by the client, not stripping off
     * path information in {@link CommonsMultipartFile#getOriginalFilename()}.
     * <p>
     * <p>
     * Default is "false", stripping off path information that may prefix the
     * actual filename e.g. from Opera. Switch this to "true" for preserving the
     * client-specified filename as-is, including potential path separators.
     *
     * @see #getOriginalFilename()
     * @see
     * @since 4.3.5
     */
    public void setPreserveFilename(boolean preserveFilename) {
        this.preserveFilename = preserveFilename;
    }


    @Override
    @NonNull
    public String getName() {
        return this.fileItem.getFieldName();
    }

    @Override
    public String getOriginalFilename() {
        String filename = this.fileItem.getName();
        if (filename == null) {
            return "";
        }
        if (this.preserveFilename) {
            return filename;
        }

        int unixSep = filename.lastIndexOf("/");
        int winSep = filename.lastIndexOf("\\");
        int pos = Math.max(winSep, unixSep);
        if (pos != -1) {
            return filename.substring(pos + 1);
        } else {
            return filename;
        }
    }

    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return (this.size == 0);
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    @NonNull
    public byte[] getBytes() {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        byte[] bytes = this.fileItem.get();
        return (bytes != null ? bytes : new byte[0]);
    }

    @Override
    @NonNull
    public InputStream getInputStream() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        InputStream inputStream = this.fileItem.getInputStream();
        return (inputStream != null ? inputStream : new ByteArrayInputStream(new byte[0]));
    }

    @Override
    public void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }

        Path destPath = dest.toPath(); // Convertir le fichier de destination en Path
        if (Files.exists(destPath)) {
            try {
                Files.delete(destPath); // Utilise Files.delete pour supprimer le fichier
            } catch (IOException e) {
                throw new IOException("Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted", e);
            }
        }

        try {
            this.fileItem.write(dest);
            if (logger.isDebugEnabled()) {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = isAvailable() ? "copied" : "moved";
                }
                logger.debug("Multipart file '" + getName() + "' with original filename [" +
                        getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
                        action + " to [" + dest.getAbsolutePath() + "]");
            }
        } catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage());
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Could not transfer to file", ex);
            throw new IOException("Could not transfer to file: " + ex.getMessage());
        }
    }

    /**
     * Determine whether the multipart content is still available.
     * If a temporary file has been moved, the content is no longer available.
     */
    protected boolean isAvailable() {
        // If in memory, it's available.
        if (this.fileItem.isInMemory()) {
            return true;
        }
        // Check actual existence of temporary file.
        if (this.fileItem instanceof DiskFileItem diskFileItem) {
            return diskFileItem.getStoreLocation().exists();
        }
        // Check whether current file size is different than original one.
        return (this.fileItem.getSize() == this.size);
    }

    /**
     * Return a description for the storage location of the multipart content.
     * Tries to be as specific as possible: mentions the file location in case
     * of a temporary file.
     */
    public String getStorageDescription() {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        } else if (this.fileItem instanceof DiskFileItem diskFileItem) {
            return "at [" + diskFileItem.getStoreLocation().getAbsolutePath() + "]";
        } else {
            return "on disk";
        }
    }

}

