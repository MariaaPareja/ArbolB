package exceptions;

public class ItemDuplicated extends Exception{ //Excepción si ya existe el item.
	public ItemDuplicated (String msg) {
		super(msg);
	}
	public ItemDuplicated() {
		super();
	}
}
