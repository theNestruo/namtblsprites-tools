package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.List;

/**
 * A NAMTBL sprite that can be read as asm code
 */
public interface NamtblSprite {

	/**
	 * @return the asm lines to render this particular sprite
	 */
	List<String> asAsm();
}
