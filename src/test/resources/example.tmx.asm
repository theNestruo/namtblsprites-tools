.EXAMPLE_0:
	ex de, hl
	ld a, $3f
	dec hl ; (-1, 0)
	ld [hl], $1f
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ld bc, -2 -1*NAMTBL_BUFFER_WIDTH  ; (-2, -1)
	add hl, bc
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	add hl, bc ; (-2, -1)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ret
.EXAMPLE_1:
	ex de, hl
	ld a, $3f
	dec hl ; (-1, 0)
	ld [hl], $1e
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ld bc, -2 -1*NAMTBL_BUFFER_WIDTH  ; (-2, -1)
	add hl, bc
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	add hl, bc ; (-2, -1)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ret
.EXAMPLE_2:
	ex de, hl
	ld a, $3f
	dec hl ; (-1, 0)
	ld [hl], $1d
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ld bc, -2 -1*NAMTBL_BUFFER_WIDTH  ; (-2, -1)
	add hl, bc
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	add hl, bc ; (-2, -1)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ret
.EXAMPLE_3:
	ex de, hl
	ld a, $3f
	dec hl ; (-1, 0)
	ld [hl], $13
	inc hl ; (+1, 0)
	ld [hl], $14
	inc hl ; (+1, 0)
	ld [hl], a
	ld bc, -2 -1*NAMTBL_BUFFER_WIDTH  ; (-2, -1)
	add hl, bc
	ld [hl], $1b
	inc hl ; (+1, 0)
	ld [hl], $1c
	inc hl ; (+1, 0)
	ld [hl], a
	add hl, bc ; (-2, -1)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	inc hl ; (+1, 0)
	ld [hl], a
	ret
.EXAMPLE_4:
	ex de, hl
	dec hl ; (-1, 0)
	ld [hl], $10
	inc hl ; (+1, 0)
	ld [hl], $11
	inc hl ; (+1, 0)
	ld [hl], $12
	ld bc, -2 -1*NAMTBL_BUFFER_WIDTH  ; (-2, -1)
	add hl, bc
	ld [hl], $15
	inc hl ; (+1, 0)
	ld [hl], $16
	inc hl ; (+1, 0)
	ld [hl], $17
	add hl, bc ; (-2, -1)
	ld [hl], $18
	inc hl ; (+1, 0)
	ld [hl], $19
	inc hl ; (+1, 0)
	ld [hl], $1a
	ret
