package exceptions;

public class ItemNoFound extends Exception { //Excepción si el item no se encuentra
	public ItemNoFound (String msg) {
		super(msg);
	}
	public ItemNoFound() {
		super();
	}
}

