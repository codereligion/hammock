<#macro class type level=0>
<#-- @ftlvariable name="type" type="com.codereligion.hammock.compiler.model.Type" -->
<#local indent>${""?left_pad(level * 4)}</#local>
<#list type.types as subtype>

    ${indent}public static final class ${subtype.name.simple} {
    ${indent}    <@class type=subtype level=level+1/>
    ${indent}}
</#list>
<#list type.closures as closure>

    <#if closure.isStateless()>
    ${indent}private enum ${closure.name.toUpperCamel()}
    <#else>
    ${indent}private static final class ${closure.name.toUpperCamel()}
    </#if>
    <#if closure.isPredicate()>
        ${indent}implements Predicate<${closure.input.type.simple}> {
    <#else>
        ${indent}implements Function<${closure.input.type.simple}, ${closure.returnType.simple}> {
    </#if>

    <#if closure.isStateless()>
        ${indent}INSTANCE;
    <#else>
        <#list closure.arguments as argument>
        ${indent}private final ${argument.type.simple} ${argument.name}; 
        </#list>
    
        ${indent}private ${closure.name.toUpperCamel()}(${closure.parameterList}) {
            <#list closure.arguments as argument>
            ${indent}this.${argument.name} = ${argument.name};
            </#list>
        ${indent}}
    </#if>

<#if closure.isPredicate()>
        ${indent}@Override
        ${indent}public boolean apply(@Nullable ${closure.input.type.simple} ${closure.input.name}) {
        <#if closure.isGraceful()>
            <#if closure.isNullTo()>
            ${indent}return ${closure.input.name} == null || ${closure.delegate}.${closure.method.toLowerCamel()}(${closure.invocationList});
            <#else>
            ${indent}return ${closure.input.name} != null && ${closure.delegate}.${closure.method.toLowerCamel()}(${closure.invocationList});
            </#if>
        <#else>
            ${indent}return ${closure.delegate}.${closure.method.toLowerCamel()}(${closure.invocationList});
        </#if>

        ${indent}}
<#else>
        ${indent}@Nullable
        ${indent}@Override
        ${indent}public ${closure.returnType.simple} apply(@Nullable ${closure.input.type.simple} ${closure.input.name}) {
        <#if closure.isGraceful()>
            ${indent}return ${closure.input.name} == null ? null : ${closure.delegate}.${closure.method.toLowerCamel()}(${closure.invocationList});
        <#else>
            ${indent}return ${closure.delegate}.${closure.method.toLowerCamel()}(${closure.invocationList});
        </#if>
        ${indent}}
</#if>

    ${indent}}
</#list>

    ${indent}private ${type.name.simple}() {

    ${indent}}
<#list type.closures as closure>

    <#if closure.isPredicate()>
    ${indent}public static Predicate<${closure.input.type.simple}> ${closure.name.toLowerCamel()}(${closure.parameterList}) {
    <#else>
    ${indent}public static Function<${closure.input.type.simple}, ${closure.returnType.simple}> ${closure.name.toLowerCamel()}(${closure.parameterList}) {
    </#if>
        <#if closure.isStateless()>
        ${indent}return ${closure.name.upperCamel}.INSTANCE;
        <#else>
        ${indent}return new ${closure.name.upperCamel}(${closure.argumentList});
        </#if>
    ${indent}}
</#list>
</#macro>