package dev.klepto.kweb3.core.util;

import com.esaulpaugh.headlong.util.FastHex;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * Utilities methods for dealing with hexadecimal strings.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public final class Hex {
    private static final String ZERO_WITH_PREFIX = "0x0";
    private static final String ZERO = "0";

    private Hex() {
    }

    /**
     * Decodes the specified hexadecimal string to a byte array.
     *
     * @param hex the hexadecimal string, may contain <code>0x</code> prefix
     * @return a byte array containing decoded hexadecimal string
     */
    public static byte @NotNull [] toByteArray(@NotNull String hex) {
        var offset = 0;
        var length = hex.length();
        if (length >= 2) {
            val first = hex.charAt(0);
            val second = hex.charAt(1);
            if (first == '0' && (second == 'x' || second == 'X')) {
                offset = 2;
            }
        }
        return FastHex.decode(hex, offset, length);
    }

    /**
     * Converts <code>byte</code> array to a hexadecimal string with <code>0x</code> prefix and leading zeros.
     *
     * @param value the integer value
     * @return a hexadecimal representation of integer
     */
    @NotNull
    public static String toHex(byte @NotNull [] value) {
        return toHex(value, true);
    }

    /**
     * Converts <code>byte</code> array to a hexadecimal string.
     *
     * @param value       the byte array value
     * @param prefix      if true, appends <code>0x</code> prefix to the resulting string
     * @apiNote this method uses a deprecated {@link String} constructor to avoid unnecessary memory allocation
     *  and to improve performance. This method is safe to use as long as the input is a valid hexadecimal string.
     * @return a hexadecimal representation of integer
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static String toHex(byte @NotNull [] value, boolean prefix) {
        if (value.length == 0) {
            return prefix ? ZERO_WITH_PREFIX : ZERO;
        }

        // TODO: This could be faster if we moved to a custom implementation
        //  which doesn't force us to deal with this intermediate array resizing
        //  and copying. It's ok for now, but could be improved.
        val hex = FastHex.encodeToBytes(value);
        if (prefix) {
            val prefixed = new byte[hex.length + 2];
            prefixed[0] = '0';
            prefixed[1] = 'x';
            System.arraycopy(hex, 0, prefixed, 2, hex.length);
            return new String(prefixed, 0, 0, prefixed.length);
        }
        return new String(hex, 0, 0, hex.length);
    }

    /**
     * Converts given hexadecimal string to {@link BigInteger}.
     *
     * @param hex the hexadecimal string
     * @return a big integer value of hexadecimal string
     */
    @NotNull
    public static BigInteger toBigInteger(@NotNull String hex) {
        return new BigInteger(toByteArray(hex));
    }

}
