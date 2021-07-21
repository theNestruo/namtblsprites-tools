.EXAMPLE_0:
	ld a, $1f
	ld (de), a
	ret
.EXAMPLE_1:
	ld a, $1e
	ld (de), a
	ret
.EXAMPLE_2:
	ld a, $1d
	ld (de), a
	ret
.EXAMPLE_3:
	ld hl, .EXAMPLE_3_DATA
	ldi
	ldi
	ld a, -1*NAMTBL_BUFFER_WIDTH -2 ; (-2, -1)
	add e
	ld e, a
	jp nc, $+4
	dec d
	ldi
	ldi
	ret
.EXAMPLE_3_DATA:
	db $13, $14, $1b, $1c
.EXAMPLE_4:
	ld hl, .EXAMPLE_4_DATA
	ldi
	ldi
	ldi
	ld a, -1*NAMTBL_BUFFER_WIDTH -3 ; (-3, -1)
	add e
	ld e, a
	jp nc, $+4
	dec d
	ldi
	ldi
	ldi
	ld a, -1*NAMTBL_BUFFER_WIDTH -3 ; (-3, -1)
	add e
	ld e, a
	jp nc, $+4
	dec d
	ldi
	ldi
	ldi
	ret
.EXAMPLE_4_DATA:
	db $10, $11, $12, $15, $16, $17, $18, $19
	db $1a
