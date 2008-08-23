package info.rvin.flexmojos.utilities;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * TODO delete when this issue is done http://jira.codehaus.org/browse/MECLIPSE-417
 * 
 * @author velo
 */
public class CompileConfigurationLoader
{

    public static String getCompilerPluginSetting( MavenProject project, String optionName )
    {
        Xpp3Dom value = getCompilerPluginConfiguration( project, optionName );
        if ( value == null )
        {
            return null;
        }
        return value.getValue();
    }

    @SuppressWarnings( "unchecked" )
    public static Xpp3Dom getCompilerPluginConfiguration( MavenProject project, String optionName )
    {
        Xpp3Dom value = findCompilerPluginSettingInPlugins( project.getModel().getBuild().getPlugins(), optionName );
        if ( value == null && project.getModel().getBuild().getPluginManagement() != null )
        {
            value =
                findCompilerPluginSettingInPlugins( project.getModel().getBuild().getPluginManagement().getPlugins(),
                                                    optionName );
        }
        return value;
    }

    /**
     * Returns a compiler plugin settings from a list of plugins .
     * 
     * @param project maven project
     * @return option value (may be null)
     */
    @SuppressWarnings( "unchecked" )
    private static Xpp3Dom findCompilerPluginSettingInPlugins( List<Plugin> plugins, String optionName )
    {

        for ( Iterator<Plugin> it = plugins.iterator(); it.hasNext(); )
        {
            Plugin plugin = (Plugin) it.next();

            if ( plugin.getArtifactId().equals( "flex-compiler-mojo" ) )
            {
                Xpp3Dom o = (Xpp3Dom) plugin.getConfiguration();

                Xpp3Dom value = null;
                // this is the default setting
                if ( o != null && o.getChild( optionName ) != null )
                {
                    value = o.getChild( optionName );
                }

                List<PluginExecution> executions = plugin.getExecutions();

                // a different source/target version can be configured for test
                // sources compilation
                for ( Iterator<PluginExecution> iter = executions.iterator(); iter.hasNext(); )
                {
                    PluginExecution execution = (PluginExecution) iter.next();
                    o = (Xpp3Dom) execution.getConfiguration();

                    if ( o != null && o.getChild( optionName ) != null )
                    {
                        value = o.getChild( optionName );
                    }
                }

                return value;
            }
        }
        return null;
    }
}
