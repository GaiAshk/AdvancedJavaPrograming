package util;

/**
 * A class that holds an attribute-value pair.
 *
 * Attributes are "HTTP tokens": a sequence of non-special non-whitespace
 * characters. The special characters are control characters, space, tab and the characters from the following set: <pre>
 * ()[]{}'"<>@,;:\/?=
 * </pre>
 * Values are arbitrary strings and may be missing.
 * @author talm
 *
 */
public class AVPair {
    public String attr;
    public String value;

    public AVPair(String attr, String value) {
        this.attr = attr;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (!(obj instanceof AVPair)))
            return false;
        AVPair other = (AVPair) obj;
        if (attr == null) {
            if (other.attr != null)
                return false;
        } else if (!attr.equals(other.attr))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (value != null)
            return attr + "=\"" + value.replaceAll("\"", "\\\"") + "\"";
        else
            return attr;
    }
}
