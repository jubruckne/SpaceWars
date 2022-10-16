package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Utils {
    public static void log(String message) {
        StackTraceElement[] stack = new Throwable().getStackTrace();

        String ctx = stack[1].getFileName().replace(".java", ".") + stack[1].getMethodName() + "(" + stack[1].getLineNumber() + ")";
        Gdx.app.log(ctx, message);
    }

    public static void log(String message, Object... args) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        message = format(message, args);

        String ctx = stack[1].getFileName().replace(".java", ".") + stack[1].getMethodName() + "(" + stack[1].getLineNumber() + ")";
        Gdx.app.log(ctx, message);
    }

    public static String format(String format, Object... args) {
        String str = format;

        for (Object arg: args) {
            if (arg instanceof Float) {
                str = str.replaceFirst("%.0f", Integer.toString(Math.round((Float) arg)));
                str = str.replaceFirst("%.1f", Float.toString(Math.round((Float) arg)));
                str = str.replaceFirst("%f", Float.toString((Float) arg));
            } else if (arg instanceof String) {
                str = str.replaceFirst("%s", arg.toString());
            } else if (arg instanceof Integer) {
                str = str.replaceFirst("%i", arg.toString());
            } else if (arg instanceof Rectangle) {
                float x, y, w, h;
                x = ((Rectangle) arg).x;
                y = ((Rectangle) arg).y;
                w = ((Rectangle) arg).width;
                h = ((Rectangle) arg).height;

                str = str.replaceFirst(
                        "%i",
                        "(" + Math.round(x)
                                + ", " + Math.round(y)
                                + ", " + Math.round(w)
                                + ", " + Math.round(h) + ")");
            } else if (arg instanceof Vector3) {
                str = str.replaceFirst("%.0f",
                        "("
                                + Math.round(((Vector3) arg).x) + ", "
                                + Math.round(((Vector3) arg).y) + ", "
                                + Math.round(((Vector3) arg).z) + ")");
                str = str.replaceFirst("%.1f",
                        "("
                                + Math.round(((Vector3) arg).x * 10) / 10f + ", "
                                + Math.round(((Vector3) arg).y * 10) / 10f + ", "
                                + Math.round(((Vector3) arg).z * 10) / 10f + ")");
                str = str.replaceFirst("%f",
                        "("
                                + ((Vector3) arg).x + ", "
                                + ((Vector3) arg).y + ", "
                                + ((Vector3) arg).z + ")");
            } else if (arg instanceof Vector2) {
                str = str.replaceFirst("%.0f",
                        "("
                                + Math.round(((Vector2) arg).x) + ", "
                                + Math.round(((Vector2) arg).y) + ")");
                str = str.replaceFirst("%.1f",
                        "("
                                + Math.round(((Vector2) arg).x * 10) / 10 + ", "
                                + Math.round(((Vector2) arg).y * 10) / 10 + ")");
                str = str.replaceFirst("%f",
                        "("
                                + ((Vector2) arg).x + ", "
                                + ((Vector2) arg).y + ")");
            } else if (arg == null) {
                str = str.replaceFirst("%", "<null>");
            } else {
                Gdx.app.log("Utils.format", arg.getClass().getCanonicalName());
                str = str.replaceFirst("%", arg.toString());
            }
        }

        return str;
    }
}
