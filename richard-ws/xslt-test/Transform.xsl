<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	<xsl:template match="chapter | sect1 | sect2">
		<xsl:copy>
			<xsl:attribute name="uid">
                <xsl:value-of select="generate-id(.)" />
            </xsl:attribute>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>	<!-- standard copy template -->
	<xsl:template match="node()|@*">
  		<xsl:copy>
  			<xsl:apply-templates select="node()|@*"/>
  		</xsl:copy>
</xsl:template></xsl:stylesheet>