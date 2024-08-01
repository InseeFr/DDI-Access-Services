<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"    
    xmlns:xs="http://www.w3.org/2001/XMLSchema"     
    xmlns:uuid="http://some.namespace.com"
    xmlns:r="ddi:reusable:3_3"
    xmlns:ddi="ddi:instance:3_3"      
    xmlns:func="http://www.w3.org/2005/xpath-functions"
    exclude-result-prefixes="xs uuid func"    
    version="3.0">
    <!--pour aider a debuger, mettre la variable à oui-->
    <xsl:variable name="debug" select="'non'"/>
    <xsl:param name="idValue"/>
    <xsl:param name="suggesterName"/>
    <xsl:param name="suggesterDescription"/>
    <xsl:param name="version"/>
    <xsl:param name="timbre"/>
	<xsl:param name="principale"/>
	<xsl:param name="secondaire"/>
	<xsl:param name="labelSecondaire"/>
	
    <xsl:output indent="yes"/>
    <!--ici la variable "maitresse"-->
    <xsl:variable name="principal" select="$principale"/>
    <!--les autres variables-->
    <xsl:variable name="keyz" select="$secondaire"/>
    <!--les noms de code liste (?), a mettre dans le meme ordre que juste au dessus--> 
    <xsl:variable name="LabelListe" select="$labelSecondaire"/>
    
    <!--je sais pas trop ^^'-->
    <xsl:variable name="ResourcePackageCitationTitle" select="concat('Paquet de ressources ',$suggesterName)"/>
    
    <!--pareil ^^'-->
    <xsl:variable name="CategorySchemeNameString" select="concat('Ensemble_categorie_',$suggesterName)"/>
    <xsl:variable name="CategorySchemeLabelContent" select="concat('Ensemble des Catégories de ',$suggesterName)"/>
    <xsl:variable name="CategorySchemeDescriptionContent" select="concat('Ensemble de catégories de ',$suggesterName)"/>
    <xsl:variable name="CodeListSchemeLabelContent" select="concat('Ensemble_CodeList_',$suggesterName)"/>
    
    
    <xsl:output indent="yes"/>     
    
    <xsl:template match="data">
        <xsl:variable name="filename">
            <xsl:value-of select="string-join((tokenize(tokenize(document-uri(/),'/')[last()],'\.')[position()!=last()]),'.')"/>
        </xsl:variable>                       
       

        <!-- Creation du xml a partir du json -->
        <xsl:variable name="xmlFROMjson">
            <xsl:copy-of select="json-to-xml(.)"/>
        </xsl:variable>                      
        
        <!--bloc de debug-->
        <xsl:if test="$debug='oui'"><xsl:text>
            ***********************xmlFROMjson********************
            
</xsl:text>
        <xsl:copy-of select="$xmlFROMjson"></xsl:copy-of>
        </xsl:if>
        
        <!-- Enrichissement avec les uuid + mise en place de la structure -->
        <xsl:variable name="xmlFROMjsonUUID">
            <all>
                <xsl:apply-templates select="$xmlFROMjson" mode="uuid1"/>
                <xsl:copy-of select="$xmlFROMjson"></xsl:copy-of>                
            </all>
        </xsl:variable>
        
        
        <!--bloc de debug-->
        <xsl:if test="$debug='oui'"><xsl:text>
                
                ***********************$xmlFROMjsonUUID********************
                
</xsl:text>
        <xsl:copy-of select="$xmlFROMjsonUUID"></xsl:copy-of>
        </xsl:if>
        
        
        <xsl:if test="$debug='oui'"><xsl:text>
                
                *********************** sortie ********************
                
</xsl:text>
        </xsl:if>
        
        <!-- Creation du ddi -->
        <xsl:apply-templates select="$xmlFROMjsonUUID">
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:apply-templates>
     
    </xsl:template>
    
    <!-- Templates pour l'enrichissement uuid -->
    
    <xsl:template match="func:array" mode="uuid1">
        <xsl:variable name="uuidMain">
            <xsl:if test="$debug='oui'">11111</xsl:if><xsl:value-of select="uuid:randomUUID()"/>
        </xsl:variable>
        <xsl:variable name="uuidCatSchRef">
            <xsl:if test="$debug='oui'">22222</xsl:if><xsl:value-of select="uuid:randomUUID()"/>
        </xsl:variable>
        <xsl:variable name="uuidCodLisSch">
            <xsl:if test="$debug='oui'">33333</xsl:if><xsl:value-of select="uuid:randomUUID()"/>
        </xsl:variable>                
        
        <FragInst>
            <TopLevRef>
                <uuid><xsl:value-of select="$uuidMain"/></uuid>
            </TopLevRef>
            <FragResPac>
                <ResPac>
                    <uuid><xsl:value-of select="$uuidMain"/></uuid>
                    <CatSchRef>
                        <uuid><xsl:value-of select="$uuidCatSchRef"/></uuid>
                    </CatSchRef>
                    <CodLisSch>
                        <uuid><xsl:value-of select="$uuidCodLisSch"/></uuid>
                    </CodLisSch>
                </ResPac>
            </FragResPac>
            
            <FragCatSch>
                <CatSch>
                    <uuid><xsl:value-of select="$uuidCatSchRef"/></uuid>
                    <xsl:apply-templates mode="uuid1"/>
                </CatSch>
            </FragCatSch>                                 
            
            <FragCodLisRef>
                <uuid><xsl:value-of select="$uuidCodLisSch"/></uuid>
                <xsl:apply-templates mode="uuid2" select="*[1]"/>
            </FragCodLisRef>
           
           
           
        </FragInst>
    </xsl:template>
    
  
    <xsl:template match="func:map" mode="uuid1">
        <xsl:variable name="i" select="position() + 3"/>
        <xsl:variable name="uuidCatRef">
            <xsl:if test="$debug='oui'"><xsl:value-of select="concat($i,$i,$i,$i,$i)"/></xsl:if><xsl:value-of select="uuid:randomUUID()"/>
        </xsl:variable>
        <CatRef>
            <uuid><xsl:copy-of select="$uuidCatRef"/></uuid>
        </CatRef>                      
    </xsl:template>
    
    
    
    <xsl:template match="func:string[@key!=$principal]" mode="uuid2">
        <xsl:variable name="i" select="position() + 5"/>
        <xsl:variable name="uuidCodLisRef">
            <xsl:if test="$debug='oui'"><xsl:value-of select="concat($i,$i,$i,$i,$i)"/></xsl:if><xsl:value-of select="uuid:randomUUID()"/>
        </xsl:variable>
        <CodLisRef>
            <uuid><xsl:copy-of select="$uuidCodLisRef"/></uuid>
        </CodLisRef>                
    </xsl:template>
    
    
   
    <!-- Template pour creation ddi -->
    
    <xsl:template match="all">  

        <xsl:param name="filename"/>                
        
        <xsl:variable name="arbre">
            <xsl:copy-of select="."></xsl:copy-of>
        </xsl:variable>
        
       
        
        
        <xsl:element name="ddi:FragmentInstance">
        
            <TopLevelReference>
                <r:Agency>fr.insee</r:Agency>
                <r:ID><xsl:value-of select="FragInst/TopLevRef/uuid"/></r:ID>
                <r:Version><xsl:value-of select="$version"/></r:Version>
                <r:TypeOfObject>ResourcePackage</r:TypeOfObject>
            </TopLevelReference>
            <Fragment xmlns="ddi:instance:3_3">
                 <xsl:namespace name="r" select="'ddi:reusable:3_3'"/>
                <ResourcePackage isUniversallyUnique="true" xmlns="ddi:group:3_3">  <!--xmlns="ddi:logicalproduct:3_3"-->
                    <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                    <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',FragInst/TopLevRef/uuid/text(),':',$version)"/></r:URN>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID><xsl:value-of select="FragInst/TopLevRef/uuid"/></r:ID>                    
                    <r:Version><xsl:value-of select="$version"/></r:Version>
                    <r:Citation>
                        <r:Title>
                            <r:String xml:lang="fr-FR"><xsl:value-of select="$ResourcePackageCitationTitle"/></r:String>
                        </r:Title>
                    </r:Citation>                                        
                    <r:CategorySchemeReference>
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID><xsl:value-of select="FragInst/FragResPac/ResPac/CatSchRef/uuid/text()"/></r:ID>
                        <r:Version><xsl:value-of select="$version"/></r:Version>
                        <r:TypeOfObject>CategoryScheme</r:TypeOfObject>
                    </r:CategorySchemeReference>
                    <r:CodeListSchemeReference>
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID><xsl:value-of select="FragInst/FragResPac/ResPac/CodLisSch/uuid/text()"/></r:ID>
                        <r:Version><xsl:value-of select="$version"/></r:Version>
                        <r:TypeOfObject>CodeListScheme</r:TypeOfObject>
                    </r:CodeListSchemeReference>
                </ResourcePackage>
            </Fragment>
            <Fragment xmlns="ddi:instance:3_3">
                <xsl:namespace name="r" select="'ddi:reusable:3_3'"/>
                <CategoryScheme isUniversallyUnique="true" xmlns="ddi:logicalproduct:3_3">  <!--xmlns="ddi:logicalproduct:3_3"-->
                    <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                    <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',FragInst/FragResPac/ResPac/CatSchRef/uuid/text(),':',$version)"/> </r:URN>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID><xsl:value-of select="FragInst/FragResPac/ResPac/CatSchRef/uuid/text()"/></r:ID>                    
                    <r:Version><xsl:value-of select="$version"/></r:Version>
                    <CategorySchemeName>
                        <r:String xml:lang="fr-FR"><xsl:value-of select="$CategorySchemeNameString"/></r:String>
                    </CategorySchemeName>
                    <r:Label>
                        <r:Content xml:lang="fr-FR"><xsl:value-of select="$CategorySchemeLabelContent"/></r:Content>
                    </r:Label>
                    <r:Description>
                        <r:Content xml:lang="fr-FR"><xsl:value-of select="$CategorySchemeDescriptionContent"/></r:Content>
                    </r:Description>
                    <xsl:apply-templates select="FragInst/FragCatSch/CatSch/CatRef"/>
                </CategoryScheme>
            </Fragment>
            
            
            <xsl:for-each select="1 to count(FragInst/FragCatSch/CatSch/CatRef)">          
                <xsl:variable name="pos" select="."/>                
                <xsl:variable name="uuidpos" select="$arbre//CatRef[$pos]/uuid/text()"/>   
                
                
                <xsl:apply-templates select="$arbre//func:array/func:map[$pos]" mode="category">
                    <xsl:with-param name="uuidpos" select="$uuidpos"/>
                </xsl:apply-templates>
                
            </xsl:for-each>
            
            
    
                                   
            <Fragment xmlns="ddi:instance:3_3">
                <xsl:namespace name="r" select="'ddi:reusable:3_3'"/>
                <CodeListScheme isUniversallyUnique="true"  xmlns="ddi:logicalproduct:3_3">
                    <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                    <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',FragInst/FragResPac/ResPac/CodLisSch/uuid/text(),':',$version)"/></r:URN>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID><xsl:value-of select="FragInst/FragResPac/ResPac/CodLisSch/uuid/text()"/></r:ID>
                    <r:Version><xsl:value-of select="$version"/></r:Version>
                    <r:Label>
                        <r:Content xml:lang="fr-FR"><xsl:value-of select="$CodeListSchemeLabelContent"/></r:Content>
                    </r:Label>
                    <xsl:apply-templates select="FragInst/FragCodLisRef/CodLisRef"/>
                </CodeListScheme>
            </Fragment>   
            
            
            
            <xsl:for-each select="1 to count(FragInst/FragCodLisRef/CodLisRef/uuid)">  
                <xsl:variable name="pos" select="."/> 
                <xsl:variable name="uuidpos" select="$arbre//FragInst/FragCodLisRef/CodLisRef[$pos]/uuid/text()"/>
                <Fragment xmlns="ddi:instance:3_3" xmlns:r="ddi:reusable:3_3">
                    <CodeList isUniversallyUnique="true" xmlns="ddi:logicalproduct:3_3">
                        <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                        <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',$uuidpos,':',$version)"/></r:URN>
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID><xsl:value-of select="$uuidpos"/></r:ID>
                        <r:Version><xsl:value-of select="$version"/></r:Version>
                        <CodeListName>
                            <r:String xml:lang="fr-FR"><xsl:value-of select="concat('CL_',$LabelListe[$pos])"/></r:String>
                        </CodeListName>
                        <r:Label>
                            <r:Content xml:lang="fr-FR"><xsl:value-of select="$LabelListe[$pos]"/></r:Content>
                        </r:Label>
                        
                        <xsl:for-each select="1 to count($arbre//func:array/func:map)">
                            <xsl:variable name="pos2" select="."/>
                            <xsl:variable name="uuidCode">
                                <xsl:value-of select="uuid:randomUUID()"/>
                            </xsl:variable>
                            <Code isUniversallyUnique="true"> 
                                <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',$uuidCode,':',$version)"/></r:URN>
                                <r:Agency>fr.insee</r:Agency>
                                <r:ID><xsl:value-of select="$uuidCode"/></r:ID>                    
                                <r:Version><xsl:value-of select="$version"/></r:Version>                                
                                <r:CategoryReference>
                                    <r:Agency>fr.insee</r:Agency>
                                    <r:ID><xsl:value-of select="$arbre//FragInst/FragCatSch/CatSch/CatRef[$pos2]/uuid/text()"/></r:ID> 
                                    <r:Version><xsl:value-of select="$version"/></r:Version>
                                    <r:TypeOfObject>Category</r:TypeOfObject>
                                </r:CategoryReference>
                                <r:Value><xsl:value-of select="$arbre//func:array/func:map[$pos2]/func:string[@key=$keyz[$pos]]"/></r:Value>
                            </Code>
                        </xsl:for-each>
                        
                        
                    </CodeList>
                </Fragment>
            </xsl:for-each>
            
            
            
            
         </xsl:element>
    </xsl:template>


    <xsl:template match="CatRef"> 
        <xsl:param name="filename"/>
        <r:CategoryReference>  
            <r:Agency>fr.insee</r:Agency>
            <r:ID><xsl:value-of select="uuid"/></r:ID>                    
            <r:Version><xsl:value-of select="$version"/></r:Version>
            <r:TypeOfObject>Category</r:TypeOfObject>
        </r:CategoryReference>
    </xsl:template>
    
    <xsl:template match="CodLisRef">             
        <xsl:param name="filename"/>
        <r:CodeListReference>  
            <r:Agency>fr.insee</r:Agency>
            <r:ID><xsl:value-of select="uuid"/></r:ID>                    
            <r:Version><xsl:value-of select="$version"/></r:Version>
            <r:TypeOfObject>CodeList</r:TypeOfObject>
        </r:CodeListReference>
    </xsl:template>
    
    
    
    <xsl:template match="func:map" mode="category">   
        <xsl:param name="uuidpos"/>        
        <Fragment xmlns="ddi:instance:3_3" xmlns:r="ddi:reusable:3_3"> 
            <Category isUniversallyUnique="true" isMissing="false" xmlns="ddi:logicalproduct:3_3">
                <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',$uuidpos,':',$version)"/></r:URN>
                <r:Agency>fr.insee</r:Agency>
                <r:ID><xsl:value-of select="$uuidpos"/></r:ID>
                <r:Version><xsl:value-of select="$version"/></r:Version>                
                <CategoryName>
                    <r:String xml:lang="fr-FR"><xsl:value-of select="concat('CAT_',*[@key=$principal]/text())"/></r:String>
                </CategoryName>
                <r:Label>
                    <r:Content xml:lang="fr-FR"><xsl:value-of select="*[@key=$principal]/text()"/></r:Content>
                </r:Label>
            </Category>
        </Fragment>
    </xsl:template>
 
</xsl:stylesheet>