{ pkgs ? import <nixpkgs> {} }:

let
    # Use the same font directories that your system already sees.
    # You can add more if needed, but this should cover the basics.
    fontDirs = [
        pkgs.noto-fonts
        pkgs.liberation_ttf
        pkgs.dejavu_fonts
    ];

    fontsConf = pkgs.makeFontsConf {
        fontDirectories = fontDirs;
    };
    in
    pkgs.mkShell {
    buildInputs = with pkgs; [
        graalvmPackages.graalvm-ce   # or any other JDK you prefer
        fontconfig
        freetype

        # Essential X11 client libraries for Java AWT/Swing
        libx11
        libxext
        libxrender
        libxtst
        libxi
        libxrandr
        libxcursor
        libxcomposite
        libxdamage
        libxfixes
        libxinerama
        libxscrnsaver
        libxcb
        libxcb-util
        libxcb-wm
        libxcb-image
        libxcb-keysyms
        libxcb-render-util

        # Additional runtime libraries sometimes needed
        alsa-lib
        gtk3
        glib
        pango
        cairo
        atk
        gdk-pixbuf
    ];

    shellHook = ''
        export FONTCONFIG_FILE="${fontsConf}"
        export FONTCONFIG_PATH="$(dirname ${fontsConf})"

        # Make sure Java can find the X11 libraries
        export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath [
            pkgs.libx11
            pkgs.libxext
            pkgs.libxrender
            pkgs.libxtst
            pkgs.libxi
            pkgs.freetype
            pkgs.fontconfig
        ]}:$LD_LIBRARY_PATH"

        echo "Fontconfig file: $FONTCONFIG_FILE"
        echo "FONTCONFIG_PATH: $FONTCONFIG_PATH"
    '';
}