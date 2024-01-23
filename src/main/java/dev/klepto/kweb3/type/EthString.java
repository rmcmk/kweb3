package dev.klepto.kweb3.type;

/**
 * Represents ethereum <code>string</code> data type.
 *
 * @param value the string value
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public record EthString(String value) implements EthType {

    /* Solidity style string initializers */
    public static EthString string(String value) {
        return new EthString(value);
    }

}