package br.com.kau4dev.appProdutos.exception;

public class PrecoDeveSerMaiorQueZeroException extends RuntimeException {
    public PrecoDeveSerMaiorQueZeroException(String message) {
        super(message);
    }
}
