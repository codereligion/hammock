<#-- @ftlvariable name="" type="com.codereligion.hammock.compiler.model.Type" -->
package ${package};

<#list imports as import>
import ${import};
</#list>

<#import "type.ftl" as t/>
@Generated("com.codereligion.hammock.compiler.FunctorCompiler")
public final class ${name.simple} {
    <@t.class type=.data_model/>       

}
