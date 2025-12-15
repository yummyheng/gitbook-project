import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Test the export functionality modification
 */
public class TestExportFunction {
    
    public static void main(String[] args) throws Exception {
        // Create mock data
        Category rootCategory = new Category(1L, "Root", 0L);
        Category subCategory1 = new Category(2L, "SubCategory1", 1L);
        Category leafCategory1 = new Category(3L, "LeafCategory1", 2L);
        
        Article article1 = new Article(1L, "Article1", "# Article1 Content\nThis is the content of Article1", 3L);
        Article article2 = new Article(2L, "Article2", "# Article2 Content\nThis is the content of Article2", 3L);
        Article article3 = new Article(3L, "RootArticle", "# RootArticle Content\nThis is the content of RootArticle", 1L);
        
        // Test the export logic
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            // Process root category (non-leaf)
            List<Article> rootArticles = new ArrayList<>();
            rootArticles.add(article3);
            List<Category> rootSubCategories = new ArrayList<>();
            rootSubCategories.add(subCategory1);
            processCategoryForExportTest(rootCategory, "", zipOut, true, rootArticles, rootSubCategories);
            
            // Process sub category (non-leaf)
            List<Article> subArticles = new ArrayList<>();
            List<Category> subSubCategories = new ArrayList<>();
            subSubCategories.add(leafCategory1);
            processCategoryForExportTest(subCategory1, "Root", zipOut, true, subArticles, subSubCategories);
            
            // Process leaf category
            List<Article> leafArticles = new ArrayList<>();
            leafArticles.add(article1);
            leafArticles.add(article2);
            List<Category> leafSubCategories = new ArrayList<>();
            processCategoryForExportTest(leafCategory1, "Root\\SubCategory1", zipOut, false, leafArticles, leafSubCategories);
        }
        
        // Save test zip file
        String zipFilePath = "D:\\aiProject\\gitbook-project\\backend\\test-export.zip";
        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            fos.write(outputStream.toByteArray());
        }
        
        System.out.println("Test export completed, zip file saved to: " + zipFilePath);
        
        // Check zip file structure
        System.out.println("\nChecking zip file structure:");
        checkZipStructure(zipFilePath);
    }
    
    /**
     * Mock the processCategoryForExport method
     */
    private static void processCategoryForExportTest(Category category, String pathPrefix, ZipOutputStream zipOut, 
                                                  boolean hasSubCategories, List<Article> articles, List<Category> subCategories)
            throws Exception {
        System.out.println("Processing category: " + category.getLabel() + ", pathPrefix: " + pathPrefix + ", hasSubCategories: " + hasSubCategories);
        
        // Process articles
        if (!articles.isEmpty()) {
            // Build path for articles
            String articlesPath;
            if (!hasSubCategories) {
                // Leaf category, don't create folder, use parent path directly
                articlesPath = pathPrefix;
            } else {
                // Non-leaf category, create folder
                articlesPath = pathPrefix.isEmpty() ? category.getLabel() : pathPrefix + File.separator + category.getLabel();
            }
            
            for (Article article : articles) {
                // Build markdown file path
                String fileName = article.getTitle() + ".md";
                String filePath;
                if (articlesPath.isEmpty()) {
                    filePath = fileName;
                } else {
                    filePath = articlesPath + File.separator + fileName;
                }
                
                // Create zip entry
                ZipEntry zipEntry = new ZipEntry(filePath);
                zipOut.putNextEntry(zipEntry);
                
                // Write markdown content
                if (article.getContent() != null) {
                    zipOut.write(article.getContent().getBytes("UTF-8"));
                }
                
                // Close zip entry
                zipOut.closeEntry();
                
                System.out.println("  Added file: " + filePath);
            }
        }
        
        // Log subcategories
        System.out.println("  Subcategories count: " + subCategories.size());
    }
    
    /**
     * Check zip file structure
     */
    private static void checkZipStructure(String zipFilePath) throws Exception {
        try (ZipInputStream zipIn = new ZipInputStream(new java.io.FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                System.out.println("- " + entry.getName());
                zipIn.closeEntry();
            }
        }
    }
    
    // Mock classes
    static class Category {
        private Long id;
        private String label;
        private Long parentId;
        
        public Category(Long id, String label, Long parentId) {
            this.id = id;
            this.label = label;
            this.parentId = parentId;
        }
        
        public Long getId() { return id; }
        public String getLabel() { return label; }
        public Long getParentId() { return parentId; }
    }
    
    static class Article {
        private Long id;
        private String title;
        private String content;
        private Long categoryId;
        
        public Article(Long id, String title, String content, Long categoryId) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.categoryId = categoryId;
        }
        
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public Long getCategoryId() { return categoryId; }
    }
}