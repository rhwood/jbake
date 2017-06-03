package org.jbake.app.configuration;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default implementation of a {@link JBakeConfiguration}
 */
public class DefaultJBakeConfiguration implements JBakeConfiguration {

    private Logger logger = LoggerFactory.getLogger(DefaultJBakeConfiguration.class);

    private static final String SOURCE_FOLDER_KEY = "sourceFolder";
    private static final String DESTINATION_FOLDER_KEY = "destinationFolder";
    private static final String ASSET_FOLDER_KEY = "assetFolder";
    private static final String TEMPLATE_FOLDER_KEY = "templateFolder";
    private static final String CONTENT_FOLDER_KEY = "contentFolder";
    private final static Pattern TEMPLATE_DOC_PATTERN = Pattern.compile("(?:template\\.)([a-zA-Z0-9-_]+)(?:\\.file)");


    private CompositeConfiguration compositeConfiguration;

    /**
     * Some deprecated implementations just need access to the configuration without access to the source folder
     * @param configuration The project configuration
     * @deprecated use {@link #DefaultJBakeConfiguration(File, CompositeConfiguration)} instead
     */
    @Deprecated
    public DefaultJBakeConfiguration(CompositeConfiguration configuration) {
        this.compositeConfiguration = configuration;
    }

    public DefaultJBakeConfiguration(File sourceFolder, CompositeConfiguration configuration) {
        this.compositeConfiguration = configuration;
        setSourceFolder(sourceFolder);
        setupDefaultDestination();
        setupPathsRelativeToSourceFile();
    }

    @Override
    public File getSourceFolder() {
        return getAsFolder(SOURCE_FOLDER_KEY);
    }

    @Override
    public File getDestinationFolder() {
        return getAsFolder(DESTINATION_FOLDER_KEY);
    }

    @Override
    public File getAssetFolder() {
        return getAsFolder(ASSET_FOLDER_KEY);
    }

    @Override
    public File getTemplateFolder() {
        return getAsFolder(TEMPLATE_FOLDER_KEY);
    }

    @Override
    public File getContentFolder() {
        return getAsFolder(CONTENT_FOLDER_KEY);
    }

    @Override
    public String getOutputExtension() {
        return getAsString(OUTPUT_EXTENSION);
    }

    @Override
    public File getTemplateFileByDocType(String docType) {
        String templateKey = "template." + docType + ".file";
        String templateFileName = getAsString(templateKey);
        if ( templateFileName != null ) {
            return new File(getTemplateFolder(), templateFileName);
        }
        logger.warn("Cannot find configuration key '{}' for document type '{}'", templateKey,docType);
        return null;
    }

    @Override
    public String getOutputExtensionByDocType(String docType) {
        String templateExtensionKey = "template."+docType+".extension";
        String defaultOutputExtension = getOutputExtension();
        return getAsString(templateExtensionKey, defaultOutputExtension);
    }

    @Override
    public boolean getPaginateIndex() {
        return getAsBoolean(PAGINATE_INDEX);
    }

    @Override
    public boolean getAssetIgnoreHidden() {
        return getAsBoolean(ASSET_IGNORE_HIDDEN);
    }

    @Override
    public boolean getUriWithoutExtension() {
        return getAsBoolean(URI_NO_EXTENSION);
    }

    @Override
    public boolean getSanitizeTag() {
        return getAsBoolean(TAG_SANITIZE);
    }

    @Override
    public boolean getRenderArchive() {
        return getAsBoolean(RENDER_ARCHIVE);
    }

    @Override
    public boolean getRenderFeed() {
        return getAsBoolean(RENDER_FEED);
    }

    @Override
    public boolean getRenderIndex() {
        return getAsBoolean(RENDER_INDEX);
    }

    @Override
    public boolean getRenderSiteMap() {
        return getAsBoolean(RENDER_SITEMAP);
    }

    @Override
    public boolean getRenderTags() {
        return getAsBoolean(RENDER_TAGS);
    }

    @Override
    public boolean getClearCache() {
        return getAsBoolean(CLEAR_CACHE);
    }

    @Override
    public String getDraftSuffix() {
        return getAsString(DRAFT_SUFFIX, "");
    }

    @Override
    public String getRenderEncoding() {
        return getAsString(RENDER_ENCODING);
    }

    @Override
    public String getTemplateEncoding() {
        return getAsString(TEMPLATE_ENCODING);
    }

    @Override
    public String getThymeleafLocale() {
        return getAsString(THYMELEAF_LOCALE);
    }

    @Override
    public String getVersion() {
        return getAsString(VERSION);
    }

    @Override
    public String getPrefixForUriWithoutExtension() {
        return getAsString(URI_NO_EXTENSION_PREFIX);
    }

    @Override
    public String getDefaultStatus() {
        return getAsString(DEFAULT_STATUS);
    }

    @Override
    public String getDefaultType() {
        String type = getAsString(DEFAULT_TYPE);
        if ( type.isEmpty() ) {
            return null;
        }
        return type;
    }

    @Override
    public String getDateFormat() {
        return getAsString(DATE_FORMAT);
    }

    @Override
    public String getArchiveFileName() {
        return getAsString(ARCHIVE_FILE);
    }

    @Override
    public String getFeedFileName() {
        return getAsString(FEED_FILE);
    }

    @Override
    public String getIndexFileName() {
        return getAsString(INDEX_FILE);
    }

    @Override
    public String getSiteMapFileName() {
        return getAsString(SITEMAP_FILE);
    }

    @Override
    public String getTagPathName() {
        return getAsString(TAG_PATH);
    }

    @Override
    public String getBuildTimeStamp() {
        return getAsString(BUILD_TIMESTAMP);
    }

    @Override
    public String getTemplateFolderName() {
        return getAsString(TEMPLATE_FOLDER);
    }

    @Override
    public String getContentFolderName() {
        return getAsString(CONTENT_FOLDER);
    }

    @Override
    public String getAssetFolderName() {
        return getAsString(ASSET_FOLDER);
    }

    @Override
    public String getExampleProjectByType(String templateType) {
        return getAsString("example.project."+templateType);
    }

    @Override
    public String getDatabaseStore() {
        return getAsString(DB_STORE);
    }

    @Override
    public String getDatabasePath() {
        return getAsString(DB_PATH);
    }

    @Override
    public int getServerPort() {
        return getAsInt(SERVER_PORT,8080);
    }

    @Override
    public int getPostsPerPage() {
        return getAsInt(POSTS_PER_PAGE, 5);
    }

    @Override
    public long getMarkdownMaxParsingTime(long defaultMaxParsingTime) {
        return getAsLong(MARKDOWN_MAX_PARSING_TIME, defaultMaxParsingTime);
    }

    @Override
    public List<String> getMarkdownExtensions() {
        return getAsList(MARKDOWN_EXTENSIONS);
    }

    @Override
    public List<String> getAsciidoctorAttributes() {
        return getAsList(ASCIIDOCTOR_ATTRIBUTES);
    }

    @Override
    public List<String> getDocumentTypes() {
        List<String> docTypes = new ArrayList<String>();
        Iterator<String> keyIterator = compositeConfiguration.getKeys();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Matcher matcher = TEMPLATE_DOC_PATTERN.matcher(key);
            if (matcher.find()) {
                docTypes.add(matcher.group(1));
            }
        }

        return docTypes;
    }

    @Override
    public boolean getExportAsciidoctorAttributes() {
        return getAsBoolean(ASCIIDOCTOR_ATTRIBUTES_EXPORT);
    }

    @Override
    public String getAttributesExportPrefixForAsciidoctor() {
        return getAsString(ASCIIDOCTOR_ATTRIBUTES_EXPORT_PREFIX, "");
    }

    @Override
    public String getSiteHost() {
        return getAsString(SITE_HOST,"http://www.jbake.org");
    }

    @Override
    public Object get(String key) {
        return compositeConfiguration.getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        compositeConfiguration.setProperty(key, value);
    }

    @Override
    public Iterator<String> getKeys() {
        return compositeConfiguration.getKeys();
    }

    @Override
    public List<String> getAsciidoctorOptionKeys() {
        List<String> options = new ArrayList<String>();
        Configuration subConfig = compositeConfiguration.subset(ASCIIDOCTOR_OPTION);

        Iterator<String> iterator = subConfig.getKeys();
        while ( iterator.hasNext() ) {
            String key = iterator.next();
            options.add(key);
        }

        return options;
    }

    @Override
    public boolean getRenderTagsIndex() {
        return compositeConfiguration.getBoolean(RENDER_TAGS_INDEX, false);
    }

    public void setRenderTagsIndex(boolean enable) {
        compositeConfiguration.setProperty(RENDER_TAGS_INDEX, enable);
    }

    public Object getAsciidoctorOption(String optionKey) {
        Configuration subConfig = compositeConfiguration.subset(ASCIIDOCTOR_OPTION);
        Object value = subConfig.getProperty(optionKey);

        if ( value == null ) {
            logger.warn("Cannot find asciidoctor option '{}.{}'", ASCIIDOCTOR_OPTION, optionKey);
            return "";
        }
        return value;
    }

    public void setSourceFolder(File sourceFolder) {
        setProperty(SOURCE_FOLDER_KEY, sourceFolder);
        setupPathsRelativeToSourceFile();
    }


    public void setDestinationFolderName(String folderName) {
        setProperty(DESTINATION_FOLDER, folderName);
        setupDefaultDestination();
    }

    public void setTemplateFolder(File templateFolder) {
        if ( templateFolder != null ) {
            setProperty(TEMPLATE_FOLDER_KEY, templateFolder);
            setProperty(TEMPLATE_FOLDER, templateFolder.getName());
        }
    }

    public void setContentFolder(File contentFolder) {
        if ( contentFolder != null ) {
            setProperty(CONTENT_FOLDER_KEY, contentFolder);
            setProperty(CONTENT_FOLDER, contentFolder.getName());
        }
    }

    public void setAssetFolder(File assetFolder) {
        if (assetFolder != null) {
            setProperty(ASSET_FOLDER_KEY, assetFolder);
            setProperty(ASSET_FOLDER, assetFolder.getName());
        }
    }

    public void setDestinationFolder(File destinationFolder) {
        if (destinationFolder != null) {
            setProperty(DESTINATION_FOLDER_KEY, destinationFolder);
            setProperty(DESTINATION_FOLDER, destinationFolder.getName());
        }
    }

    public void setAssetIgnoreHidden(boolean assetIgnoreHidden) {
        setProperty(ASSET_IGNORE_HIDDEN, assetIgnoreHidden);
    }

    public void setOutputExtension(String outputExtension) {
        setProperty(OUTPUT_EXTENSION, outputExtension);
    }

    public void setTemplateExtensionForDocType(String docType, String extension) {
        String templateExtensionKey = "template."+docType+".extension";
        setProperty(templateExtensionKey, extension);
    }

    public void setTemplateFileNameForDocType(String docType, String fileName) {
        String templateKey = "template." + docType + ".file";
        setProperty(templateKey, fileName);
    }

    public void setPaginateIndex(boolean paginateIndex) {
        setProperty(PAGINATE_INDEX, paginateIndex);
    }

    public void setPostsPerPage(int postsPerPage) {
        setProperty(POSTS_PER_PAGE, postsPerPage);
    }

    public void setUriWithoutExtension(boolean withoutExtension) {
        setProperty(URI_NO_EXTENSION, withoutExtension);
    }

    public void setPrefixForUriWithoutExtension(String prefix) {
        setProperty(URI_NO_EXTENSION_PREFIX, prefix);
    }

    public void setMarkdownExtensions(String... extensions) {
        setProperty(MARKDOWN_EXTENSIONS, StringUtils.join(extensions, ","));
    }

    public void setClearCache(boolean clearCache) {
        setProperty(CLEAR_CACHE, clearCache);
    }

    public void setServerPort(int port) {
        setProperty(SERVER_PORT, port);
    }

    public void setExampleProject(String type, String fileName) {
        String projectKey = "example.project."+type;
        setProperty(projectKey, fileName);
    }

    public void setDatabaseStore(String storeType) {
        setProperty(DB_STORE, storeType);
    }

    public void setDatabasePath(String path) {
        setProperty(DB_PATH, path);
    }

    public void setDefaultStatus(String status) {
        setProperty(DEFAULT_STATUS, status);
    }

    public void setDefaultType(String type) {
        setProperty(DEFAULT_TYPE, type);
    }

    public void setSiteHost(String siteHost) {
        setProperty(SITE_HOST, siteHost);
    }

    public CompositeConfiguration getCompositeConfiguration() {
        return compositeConfiguration;
    }

    public void setCompositeConfiguration(CompositeConfiguration configuration) {
        this.compositeConfiguration = configuration;
    }

    private File getAsFolder(String key) {
        return (File) get(key);
    }

    private String getAsString(String key) {
        return compositeConfiguration.getString(key);
    }

    private String getAsString(String key, String defaultValue) {
        return compositeConfiguration.getString(key, defaultValue);
    }

    private boolean getAsBoolean(String key) {
        return compositeConfiguration.getBoolean(key, false);
    }

    private int getAsInt(String key, int defaultValue) {
        return compositeConfiguration.getInt(key, defaultValue);
    }

    private long getAsLong(String key, long defaultValue) {
        return compositeConfiguration.getLong(key, defaultValue);
    }

    private List<String> getAsList(String key) {
        return Arrays.asList(compositeConfiguration.getStringArray(key));
    }

    private void setupPathsRelativeToSourceFile() {
        setupDefaultAssetFolder();
        setupDefaultTemplateFolder();
        setupDefaultContentFolder();
    }

    private void setupDefaultContentFolder() {
        setContentFolder(new File(getSourceFolder(),getContentFolderName()));
    }

    private void setupDefaultDestination() {
        String destinationPath = getAsString(DESTINATION_FOLDER);
        setDestinationFolder(new File(getSourceFolder(),destinationPath));
    }

    private void setupDefaultAssetFolder() {
        String assetFolder = getAsString(ASSET_FOLDER);
        setAssetFolder(new File(getSourceFolder(), assetFolder));
    }

    private void setupDefaultTemplateFolder() {
        String destinationPath = getAsString(TEMPLATE_FOLDER);
        setTemplateFolder(new File(getSourceFolder(),destinationPath));
    }

    @Override
    public String getHeaderSeparator() {
        return getAsString(HEADER_SEPARATOR);
    }

    public void setHeaderSeparator(String headerSeparator) {
        setProperty(HEADER_SEPARATOR, headerSeparator);
    }

}
