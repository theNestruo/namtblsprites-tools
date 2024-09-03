.EXAMPLE_0_L: ; 1x1
.EXAMPLE_0_R: ; 1x1
	ld	a, $1f
	ld	(de), a
	ret
.EXAMPLE_1_L: ; 1x1
.EXAMPLE_1_R: ; 1x1
	ld	a, $1e
	ld	(de), a
	ret
.EXAMPLE_2_L: ; 1x1
.EXAMPLE_2_R: ; 1x1
	ld	a, $1d
	ld	(de), a
	ret
.EXAMPLE_3_L: ; 2x2
	dec	de	; (-1, 0)
.EXAMPLE_3_R: ; 2x2
	ex	de, hl
	ld	(hl), $13
	inc	hl	; (+1, 0)
	ld	(hl), $14
	ld	bc, -1 -1*NAMTBL_BUFFER_WIDTH	; (-1, -1)
	add	hl, bc	; (-1, -1)
	ld	(hl), $1b
	inc	hl	; (+1, 0)
	ld	(hl), $1c
	ret
.EXAMPLE_4_L: ; 3x3
.EXAMPLE_4_R: ; 3x3
	ex	de, hl
	dec	hl	; (-1, 0)
	ld	(hl), $10
	inc	hl	; (+1, 0)
	ld	(hl), $11
	inc	hl	; (+1, 0)
	ld	(hl), $12
	ld	bc, -2 -1*NAMTBL_BUFFER_WIDTH	; (-2, -1)
	add	hl, bc	; (-2, -1)
	ld	(hl), $15
	inc	hl	; (+1, 0)
	ld	(hl), $16
	inc	hl	; (+1, 0)
	ld	(hl), $17
	add	hl, bc	; (-2, -1)
	ld	(hl), $18
	inc	hl	; (+1, 0)
	ld	(hl), $19
	inc	hl	; (+1, 0)
	ld	(hl), $1a
	ret
