<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:ddi="ddi:instance:3_3"
    xmlns:ddi2="ddi:group:3_3"
    xmlns:r="ddi:reusable:3_3"
    xmlns:log="ddi:logicalproduct:3_3"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:param name="idepUtilisateur"/>
    <xsl:output method="text"/>
    
    <xsl:variable name="version">1</xsl:variable>
    <xsl:variable name="ItemFormat">DC337820-AF3A-4C0B-82F9-CF02535CDE83</xsl:variable>
    <xsl:variable name="VersionResponsibility">Aremplir</xsl:variable>
    <xsl:variable name="IsPublished">false</xsl:variable>
    <xsl:variable name="IsDeprecated">false</xsl:variable>
    <xsl:variable name="IsProvisional">false</xsl:variable>
    
    
    <xsl:variable name="sep">,
</xsl:variable>
    
    
    <xsl:variable name="contenu">
        <xsl:apply-templates select="ddi:FragmentInstance"/>        
    </xsl:variable>
    
    <xsl:template match="ddi:FragmentInstance">
        <xsl:apply-templates select="ddi:Fragment"/>
    </xsl:template>
    
    <xsl:template match="ddi:Fragment">
        <xsl:variable name="item"><xsl:apply-templates select="." mode="inline"/></xsl:variable>
        <item>{<xsl:apply-templates><xsl:with-param name="item" select="$item"/></xsl:apply-templates>}</item>
    </xsl:template>
    
    <xsl:template match="ddi2:ResourcePackage"><xsl:param name="item"/>
        "ItemType": "679a61f5-4246-4c89-b482-924dec09af98",
        "AgencyId": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,r:Agency,&quot;&quot;&quot;&quot;)"/>,
        "Version": <xsl:value-of select="r:Version"/>,
        "Identifier": "<xsl:value-of select="r:ID"/>",
        "Item": "<xsl:value-of select="$item"/>",
        "VersionDate": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,@versionDate,&quot;&quot;&quot;&quot;)" />,
        "VersionResponsibility": "<xsl:value-of select="$idepUtilisateur"/>",
        "IsPublished": <xsl:value-of select="$IsPublished"/>,
        "IsDeprecated": <xsl:value-of select="$IsDeprecated"/>,
        "IsProvisional": <xsl:value-of select="$IsProvisional"/>,
        "ItemFormat": "<xsl:value-of select="$ItemFormat"/>"</xsl:template>
    
    <xsl:template match="log:CategoryScheme"><xsl:param name="item"/>
        "ItemType": "1c11de94-a36d-4d80-95dc-950c6f37f624",
        "AgencyId": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,r:Agency,&quot;&quot;&quot;&quot;)"/>,
        "Version": <xsl:value-of select="r:Version"/>,
        "Identifier": "<xsl:value-of select="r:ID"/>",
        "Item": "<xsl:value-of select="$item"/>",
        "VersionDate": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,@versionDate,&quot;&quot;&quot;&quot;)" />,
        "VersionResponsibility": "<xsl:value-of select="$idepUtilisateur"/>",
        "IsPublished": <xsl:value-of select="$IsPublished"/>,
        "IsDeprecated": <xsl:value-of select="$IsDeprecated"/>,
        "IsProvisional": <xsl:value-of select="$IsProvisional"/>,
        "ItemFormat": "<xsl:value-of select="$ItemFormat"/>"</xsl:template>
    
    <xsl:template match="log:CodeListScheme"><xsl:param name="item"/>
        "ItemType": "4193d389-b5ae-4368-b399-cd5a7ee3653c",
        "AgencyId": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,r:Agency,&quot;&quot;&quot;&quot;)"/>,
        "Version": <xsl:value-of select="r:Version"/>,
        "Identifier": "<xsl:value-of select="r:ID"/>",
        "Item": "<xsl:value-of select="$item"/>",
        "VersionDate": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,@versionDate,&quot;&quot;&quot;&quot;)" />,
        "VersionResponsibility": "<xsl:value-of select="$idepUtilisateur"/>",
        "IsPublished": <xsl:value-of select="$IsPublished"/>,
        "IsDeprecated": <xsl:value-of select="$IsDeprecated"/>,
        "IsProvisional": <xsl:value-of select="$IsProvisional"/>,
        "ItemFormat": "<xsl:value-of select="$ItemFormat"/>"</xsl:template>
    
    <xsl:template match="log:CodeList"><xsl:param name="item"/>
        "ItemType": "8b108ef8-b642-4484-9c49-f88e4bf7cf1d",
        "AgencyId": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,r:Agency,&quot;&quot;&quot;&quot;)"/>,
        "Version": <xsl:value-of select="r:Version"/>,
        "Identifier": "<xsl:value-of select="r:ID"/>",
        "Item": "<xsl:value-of select="$item"/>",
        "VersionDate": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,@versionDate,&quot;&quot;&quot;&quot;)" />,
        "VersionResponsibility": "<xsl:value-of select="$idepUtilisateur"/>",
        "IsPublished": <xsl:value-of select="$IsPublished"/>,
        "IsDeprecated": <xsl:value-of select="$IsDeprecated"/>,
        "IsProvisional": <xsl:value-of select="$IsProvisional"/>,
        "ItemFormat": "<xsl:value-of select="$ItemFormat"/>"</xsl:template>
    
    <xsl:template match="log:Category"><xsl:param name="item"/>
        "ItemType": "7e47c269-bcab-40f7-a778-af7bbc4e3d00",
        "AgencyId": <xsl:value-of select="concat(&quot;&quot;&quot;&quot;,r:Agency,&quot;&quot;&quot;&quot;)"/>,
        "Version": <xsl:value-of select="$version"/>,
        "Identifier": "<xsl:value-of select="r:ID"/>",
        "Item": "<xsl:value-of select="$item"/>",
        "VersionResponsibility": "<xsl:value-of select="$idepUtilisateur"/>",
        "IsPublished": <xsl:value-of select="$IsPublished"/>,
        "IsDeprecated": <xsl:value-of select="$IsDeprecated"/>,
        "IsProvisional": <xsl:value-of select="$IsProvisional"/>,
        "ItemFormat": "<xsl:value-of select="$ItemFormat"/>"</xsl:template>
    
    <xsl:template match="*" mode="inline">
        <xsl:variable name="nom" select="name()"/>
        <xsl:variable name="sarko"><xsl:for-each select="namespace-node()"><xsl:value-of select="name()"/></xsl:for-each></xsl:variable>
        <xsl:value-of select="concat('&lt;',$nom)"/>        
        <xsl:for-each select="@*"><xsl:value-of select="concat(' ',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>
        <xsl:value-of select="'>'"/>
        <xsl:apply-templates select="*" mode="inline"/>
        <xsl:if test="count(*)=0">
            <xsl:value-of select="replace(normalize-space(.),'&quot;','\\&quot;')"/>
        </xsl:if>        
        <xsl:value-of select="concat('&lt;/',$nom,'>')"/>
    </xsl:template>
    

    <xsl:template match="ddi:Fragment" mode="inline">
        <xsl:variable name="nom" select="name()"/>
        <xsl:variable name="sarko"><xsl:for-each select="namespace-node()"><xsl:value-of select="name()"/></xsl:for-each></xsl:variable>
        <xsl:value-of select="concat('&lt;',$nom)"/>
        <xsl:for-each select="namespace-node()[name()='']"><xsl:value-of select="concat(' xmlns',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>
        <xsl:for-each select="namespace-node()[name()='r']"><xsl:value-of select="concat(' xmlns:',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>
        <xsl:for-each select="@*"><xsl:value-of select="concat(' ',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>
        <xsl:value-of select="'>'"/>
        <xsl:apply-templates select="*" mode="inline"/>      
        <xsl:value-of select="concat('&lt;/',$nom,'>')"/>
    </xsl:template>
    
    <xsl:template match="log:CodeList" mode="inline">
        <xsl:variable name="nom" select="name()"/>
        <xsl:variable name="sarko"><xsl:for-each select="namespace-node()"><xsl:value-of select="name()"/></xsl:for-each></xsl:variable>
        <xsl:value-of select="concat('&lt;',$nom)"/>
        <xsl:for-each select="namespace-node()[name()='']"><xsl:value-of select="concat(' xmlns',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>        
        <xsl:for-each select="@*"><xsl:value-of select="concat(' ',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>
        <xsl:value-of select="'>'"/>
        <xsl:apply-templates select="*" mode="inline"/>        
        <xsl:value-of select="concat('&lt;/',$nom,'>')"/>
    </xsl:template>
    
    <xsl:template match="log:Category" mode="inline">
        <xsl:variable name="nom" select="name()"/>
        <xsl:variable name="sarko"><xsl:for-each select="namespace-node()"><xsl:value-of select="name()"/></xsl:for-each></xsl:variable>
        <xsl:value-of select="concat('&lt;',$nom)"/>
        <xsl:for-each select="namespace-node()[name()='']"><xsl:value-of select="concat(' xmlns',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>        
        <xsl:for-each select="@*"><xsl:value-of select="concat(' ',name())"/>=\"<xsl:value-of select="data()"/>\"</xsl:for-each>
        <xsl:value-of select="'>'"/>
        <xsl:apply-templates select="*" mode="inline"/>
        <xsl:value-of select="concat('&lt;/',$nom,'>')"/>
    </xsl:template>
    
    
    
    
    <xsl:template match="/">{
   "Items":
[
<xsl:value-of select="string-join($contenu//item/text(),$sep)"/>       
]
}
    </xsl:template>   
</xsl:stylesheet>