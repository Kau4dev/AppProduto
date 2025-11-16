package br.com.kau4dev.appProdutos.exception;

public class EstoqueNaoEncontradoException extends RuntimeException {
    public EstoqueNaoEncontradoException(String message) {
        super(message);
    }
}
