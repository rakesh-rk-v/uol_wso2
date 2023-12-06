<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:m0="http://services.samples" version="2.0" exclude-result-prefixes="m0 fn">
   <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />
   <xsl:template match="/">
      <Payment>
         <xsl:for-each select="//order/lunch[contains(drinkName, 'Coffee')]">
            <discount>
               <xsl:value-of select="drinkPrice" />
            </discount>
         </xsl:for-each>
      </Payment>
   </xsl:template>
</xsl:stylesheet>