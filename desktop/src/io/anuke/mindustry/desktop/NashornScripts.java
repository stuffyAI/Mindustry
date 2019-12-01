package io.anuke.mindustry.desktop;

import io.anuke.arc.*;
import io.anuke.arc.files.*;
import io.anuke.arc.util.*;
import io.anuke.mindustry.mod.*;
import io.anuke.mindustry.mod.Mods.*;
import jdk.nashorn.api.scripting.*;

import javax.script.*;
import java.io.*;

public class NashornScripts extends Scripts{
    private static final Class[] denied = {FileHandle.class, InputStream.class, File.class, Scripts.class, Files.class, ClassAccess.class};
    private final String wrapper;
    private final ScriptEngine engine;
    private final SecurityManager manager;

    public NashornScripts(){
        Time.mark();
        System.setProperty("nashorn.args", "--language=es6");
        engine = new NashornScriptEngineFactory().getScriptEngine(ClassAccess.allowedClassNames::contains);
        manager = new SecurityManager();

        wrapper = Core.files.internal("scripts/wrapper.js").readString();
        run(Core.files.internal("scripts/global.js").readString());
        Log.info("Time to load script engine: {0}", Time.elapsed());
    }

    @Override
    public void run(LoadedMod mod, FileHandle file){
        run(wrapper.replace("$SCRIPT_NAME$", mod.name + "_" +file.nameWithoutExtension().replace("-", "_").replace(" ", "_")).replace("$CODE$", file.readString()));
    }

    private void run(String script){
        try{
            engine.eval(script);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
}
