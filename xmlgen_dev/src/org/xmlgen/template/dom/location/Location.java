package org.xmlgen.template.dom.location;

public class Location {

   public static final String LOCATION = "locationKey";

   public Location(String systemId,
   		          int startLine, int startColumn,
                    int endLine, int endColumn) 
   {
       super();
       this.systemId = systemId;
       this.startLine = startLine;
       this.startColumn = startColumn;
       this.endLine = endLine;
       this.endColumn = endColumn;
   }

   public String getSystemId() 
   {
       return systemId;
   }

   public int getStartLine() 
   {
       return startLine;
   }

   public int getStartColumn() 
   {
       return startColumn;
   }

   public int getEndLine() 
   {
       return endLine;
   }

   public int getEndColumn() 
   {
       return endColumn;
   }
   
   private final String systemId;
   private final int startLine;
   private final int startColumn;
   private final int endLine;
   private final int endColumn;
}