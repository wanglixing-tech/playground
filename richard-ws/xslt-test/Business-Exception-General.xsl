<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
		xmlns:bus="http://fcl.crs/xmlns/integration/esb/1.0/event/business">
	<xsl:template match="bus:Business">
		<html>
			<body>
				<h2>Business Exception</h2>
				<table border="1">
					<tr bgcolor="#9acd32" >
						<th>Index</th>
						<th>Name</th>
						<th>Value</th>
					</tr>
					<xsl:apply-templates select="bus:Activity"/>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="bus:Activity">
		<xsl:for-each select="@bus:name">
		
			<xsl:if test="not(../@bus:index)"> 
				<tr style="color: blue;">
					<td>-</td>
					<td><xsl:value-of select="../@bus:name"/></td>
					<td><xsl:value-of select="../@bus:value"/></td>
				</tr>
			</xsl:if>
			<xsl:if test="../@bus:index"> 
				<tr style="color: red;">
					<td><xsl:value-of select="../@bus:index"/></td>
					<td><xsl:value-of select="../@bus:name"/></td>
					<td><xsl:value-of select="../@bus:value"/></td>
				</tr>
			</xsl:if>
			
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
