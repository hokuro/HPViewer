package basashi.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

public class FMLInjectionData
{
    static File minecraftHome;
    static String major;
    static String minor;
    static String rev;
    static String build;
    static String mccversion;
    static String mcpversion;

    public static final List<String> containers = new ArrayList<String>();

    static void build(File mcHome)
    {
        minecraftHome = new File(FMLPaths.GAMEDIR.get().toString());
        major = new Integer(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().getMinorVersion()).toString();
        minor = new Integer(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().getMinorVersion()).toString();
        rev = new Integer(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().getMinorVersion()).toString();
        build = new Integer(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().getMinorVersion()).toString();
        mccversion = new Integer(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().getMinorVersion()).toString();
        mcpversion =new Integer(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().getMinorVersion()).toString();
    }

    static String debfuscationDataName()
    {
        return "/deobfuscation_data-"+mccversion+".lzma";
    }
    public static Object[] data()
    {
        return new Object[] { major, minor, rev, build, mccversion, mcpversion, minecraftHome, containers };
    }
}