To generate this thing:

knoxg@filament /c/Documents and Settings/knoxg/My Documents/Visual Studio Projects/OpenDMX/OpenDMX/obj/Debug
$ /c/WINDOWS/Microsoft.NET/Framework/v2.0.50727/regasm.exe OpenDMX.dll /regfile:regasm.reg
Microsoft (R) .NET Framework Assembly Registration Utility 2.0.50727.3053
Copyright (C) Microsoft Corporation 1998-2004.  All rights reserved.

Registry script 'C:\Documents and Settings\knoxg\My Documents\Visual Studio Projects\OpenDMX\OpenDMX\obj\Debug\regasm.reg' generated successfully

knoxg@filament /c/Documents and Settings/knoxg/My Documents/Visual Studio Projects/OpenDMX/OpenDMX/obj/Debug
$


although now it's visual studio 2008, so probably better to run this:

C:\java\prog\jacobgen_0.10>"c:/WINDOWS/Microsoft.NET/Framework/v2.0.50727/regasm.exe" "C:\Documents and Settings\knoxg\My Documents\Visual Studio 2008\Projects\OpenDMX2\OpenDMX\bin\OpenDMX.dll" /regfile:regasm.reg

and then to generate the jacob wrapper:

C:\java\prog\jacobgen_0.10>docs\run_jacobgen.bat -package:com.jacobgen.opendmx -destdir:output OpenDMX.tlb
