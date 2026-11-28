"""The Guerk palette: the five colors of the Guerk Ingot texture, for every Guerk texture added with the port.

The original item and block textures are drawn in these colors already (the Guerk Block adds its own in-between
shades on purpose, five colors are too coarse for it), so they are left as they are and not checked here.

    python3 tools/guerk_palette.py check    lists Guerk-colored pixels of the new textures that are not exactly in the palette
    python3 tools/guerk_palette.py fix      snaps those pixels to the closest palette color
    python3 tools/guerk_palette.py molten   generates the Molten Guerk fluid textures (Tinkers' Construct)

Run from the repository root. Needs Pillow. White highlights (#FFFFFF) are left alone on purpose.
"""
import json
import math
import os
import random
import sys

from PIL import Image

# Dark to bright, as in textures/items/guerk_ingot.png.
GUERK = ['#660066', '#800080', '#BE29EC', '#D896FF', '#EFBBFF']

TEXTURES = 'src/main/resources/assets/guerk/textures'
# Guerk textures added with the port. Add new ones here; other pixels in them (stone, sticks, ...) aren't touched.
GUERK_TEXTURES = ['blocks/molten_guerk_still.png', 'blocks/molten_guerk_flow.png']
# Pixels further away than this (CIELAB delta E) from every palette color aren't Guerk (e.g. white highlights).
MAX_DISTANCE = 15


def rgb(hex_color):
    h = hex_color.lstrip('#')
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4))


PALETTE = [rgb(c) for c in GUERK]


def lab(color):
    def linear(u):
        u /= 255.0
        return ((u + 0.055) / 1.055) ** 2.4 if u > 0.04045 else u / 12.92

    def f(t):
        return t ** (1 / 3.0) if t > 0.008856 else 7.787 * t + 16 / 116.0

    r, g, b = (linear(c) for c in color[:3])
    x = (0.4124 * r + 0.3576 * g + 0.1805 * b) / 0.95047
    y = 0.2126 * r + 0.7152 * g + 0.0722 * b
    z = (0.0193 * r + 0.1192 * g + 0.9505 * b) / 1.08883
    return 116 * f(y) - 16, 500 * (f(x) - f(y)), 200 * (f(y) - f(z))


PALETTE_LAB = [lab(c) for c in PALETTE]


def closest(color):
    """(palette color, distance) for a pixel."""
    distance, index = min((math.dist(lab(color), p), i) for i, p in enumerate(PALETTE_LAB))
    return PALETTE[index], distance


def off_palette(path):
    """Guerk-colored pixels of a texture that aren't exactly a palette color: {color: (palette color, distance, count)}."""
    found = {}
    image = Image.open(path).convert('RGBA')
    for y in range(image.height):
        for x in range(image.width):
            pixel = image.getpixel((x, y))
            if pixel[3] == 0 or pixel[:3] in PALETTE: continue
            target, distance = closest(pixel)
            if distance <= MAX_DISTANCE:
                entry = found.get(pixel[:3], (target, distance, 0))
                found[pixel[:3]] = (target, distance, entry[2] + 1)
    return found


def check():
    clean = True
    for name in GUERK_TEXTURES:
        for color, (target, distance, count) in sorted(off_palette(os.path.join(TEXTURES, name)).items()):
            clean = False
            print('%-32s #%02X%02X%02X x%-3d -> #%02X%02X%02X (delta E %.1f)' % ((name,) + color + (count,) + target + (distance,)))
    if clean: print('All new Guerk textures use the palette.')
    return clean


def fix():
    for name in GUERK_TEXTURES:
        path = os.path.join(TEXTURES, name)
        if not off_palette(path): continue
        image = Image.open(path).convert('RGBA')
        for y in range(image.height):
            for x in range(image.width):
                pixel = image.getpixel((x, y))
                if pixel[3] == 0 or pixel[:3] in PALETTE: continue
                target, distance = closest(pixel)
                if distance <= MAX_DISTANCE: image.putpixel((x, y), target + (pixel[3],))
        image.save(path)
        print('fixed', name)


def molten():
    """Animated lava-like waves, only in palette colors. Tileable: integer wave frequencies over the tile size."""
    rnd = random.Random(1234)
    waves = [(rnd.randint(1, 3), rnd.randint(1, 3), rnd.random() * 2 * math.pi, rnd.choice([-1, 1]) * rnd.randint(1, 2),
              0.5 + rnd.random()) for _ in range(6)]
    # How much of the surface each color gets, dark to bright: mostly dark, molten bright streaks on top.
    levels = [0.30, 0.58, 0.80, 0.93]

    def color(x, y, t, size):
        s = n = 0.0
        for fx, fy, phase, speed, amplitude in waves:
            s += amplitude * math.sin(2 * math.pi * (fx * x / size + fy * y / size + speed * t) + phase)
            n += amplitude
        v = 0.5 + 0.5 * math.tanh(1.8 * s / n)
        return PALETTE[sum(v > level for level in levels)] + (255,)

    def make(size, frames, flow):
        image = Image.new('RGBA', (size, size * frames))
        for f in range(frames):
            for y in range(size):
                for x in range(size):
                    # The flowing texture scrolls downwards, the still one moves in place.
                    yy = (y - f * size / frames) % size if flow else y
                    image.putpixel((x, f * size + y), color(x, yy, 0 if flow else f / frames, size))
        return image

    blocks = os.path.join(TEXTURES, 'blocks')
    make(16, 32, False).save(os.path.join(blocks, 'molten_guerk_still.png'))
    make(32, 32, True).save(os.path.join(blocks, 'molten_guerk_flow.png'))
    for name, frametime in (('molten_guerk_still.png', 2), ('molten_guerk_flow.png', 1)):
        with open(os.path.join(blocks, name + '.mcmeta'), 'w') as mcmeta:
            json.dump({'animation': {'frametime': frametime}}, mcmeta)
    print('generated the Molten Guerk textures')


if __name__ == '__main__':
    command = sys.argv[1] if len(sys.argv) > 1 else 'check'
    if command == 'check': sys.exit(0 if check() else 1)
    elif command == 'fix': fix()
    elif command == 'molten': molten()
    else: sys.exit(__doc__)
