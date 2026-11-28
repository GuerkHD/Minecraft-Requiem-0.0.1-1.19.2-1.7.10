"""
Generates the stand skins (64x64: classic 64x32 biped layout on top, extra model parts below) and the
Sheer Heart Attack texture, plus preview sheets.

usage: python3 tools/stand_textures.py src/main/resources/assets/minecraftrequiem/textures/entity <preview dir>
(on the split branch: stands/src/main/resources/...). Needs Pillow. Each stand is a palette plus small pixel grids.
"""
import random
import sys

from PIL import Image, ImageDraw

OUT, PREVIEW = sys.argv[1], sys.argv[2]


def hexc(h):
    h = h.lstrip('#')
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,)


def shade(c, rnd, amount=0.07):
    if c[3] == 0:
        return c
    f = 1 + rnd.uniform(-amount, amount)
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (255,)


class Painter:
    def __init__(self, img, palette, seed):
        self.img, self.pal, self.rnd = img, {k: hexc(v) for k, v in palette.items()}, random.Random(seed)

    def face(self, x, y, w, h, art, mirror=False):
        """art: a single palette char (fill) or a list of h strings of length w."""
        for j in range(h):
            for i in range(w):
                if isinstance(art, str):
                    ch = art
                else:
                    row = art[j]
                    ch = row[w - 1 - i] if mirror else row[i]
                if ch == '.':
                    continue
                self.img.putpixel((x + i, y + j), shade(self.pal[ch], self.rnd))

    def box(self, u, v, w, h, d, top, bottom, right, front, left, back):
        """Minecraft box UV layout for ModelRenderer.addBox(..., w, h, d) at texture offset (u, v)."""
        self.face(u + d, v, w, d, top)
        self.face(u + d + w, v, w, d, bottom)
        self.face(u, v + d, d, h, right)
        self.face(u + d, v + d, w, h, front)
        self.face(u + d + w, v + d, d, h, left)
        self.face(u + d + w + d, v + d, w, h, back)


def side_of(rows):
    """The left side of a head is the mirror image of the right side."""
    return [r[::-1] for r in rows]


# Each stand: palette, base colour (fills), head front/side/back, body front/back, arm, leg, extras.
STANDS = {
    'the_world': dict(
        palette={'Y': '#D9A82E', 'y': '#F2CF63', 'G': '#2F5D3B', 'g': '#4F8A4C', 'k': '#151515', 'S': '#EAD9A8'},
        base='Y',
        head_front=['YYYYYYYY', 'YyYYYYyY', 'YGGGGGGY', 'YSkSSkSY', 'YSSSSSSY', 'YSSGGSSY', 'GYSSSSYG', 'YGYYYYGY'],
        head_side=['YYYYYYYY', 'YYYYYyYY', 'YYGGGGGG', 'YYYYYSSS', 'YYYGYSSS', 'YYYYYSSS', 'YYYYYYSG', 'YYYYYYGG'],
        head_back=['YYYYYYYY', 'YYyYYyYY', 'YGGGGGGY', 'YYYYYYYY', 'YYYGGYYY', 'YYYGGYYY', 'YYYYYYYY', 'YYGGGGYY'],
        head_top=['YYYGGYYY'] * 8,
        body_front=['YGGYYGGY', 'GGyGGyGG', 'GYYGGYYG', 'YYYGGYYY', 'YyYGGYyY', 'YYYGGYYY', 'YGGYYGGY', 'YYGYYGYY',
                    'YYYYYYYY', 'GGGGGGGG', 'GYYkkYYG', 'YYYYYYYY'],
        body_back=['YYYYYYYY', 'YGYYYYGY', 'YYGYYGYY', 'YYYGGYYY', 'YYYGGYYY', 'YYYGGYYY', 'YYYGGYYY', 'YYGYYGYY',
                   'YYYYYYYY', 'GGGGGGGG', 'YYYYYYYY', 'YYYYYYYY'],
        arm=['GYYG', 'GGGG', 'YGGY', 'YYYY', 'YyYY', 'YYYY', 'YYYY', 'YYyY', 'GGGG', 'YYYY', 'yYYy', 'YYYY'],
        leg=['YYYY', 'YYYY', 'YyYY', 'GYYG', 'GGGG', 'YGGY', 'YYYY', 'YYyY', 'YYYY', 'GGGG', 'YYYY', 'YYYY'],
    ),
    'd4c': dict(
        palette={'B': '#9EC3E6', 'b': '#CFE2F3', 'D': '#2A3F8F', 'k': '#141414', 'w': '#F5F5F5', 'P': '#E7A1C4'},
        base='B',
        head_front=['BBBBBBBB', 'BDBBBBDB', 'BBDBBDBB', 'BkkBBkkB', 'BkwBBkwB', 'BBBDDBBB', 'BBDDDDBB', 'DBBBBBBD'],
        head_side=['BBBBBBBB', 'BDDBBBBB', 'BBBBBBDB', 'BBDBBBBB', 'BBBBBBBk', 'BBDBBBBB', 'BBBBBBBB', 'DDBBBBBD'],
        head_back=['BBDBBDBB', 'BBDBBDBB', 'BDBBBBDB', 'BDBBBBDB', 'BBDBBDBB', 'BBDBBDBB', 'BBBBBBBB', 'DDBBBBDD'],
        head_top=['BBDBBDBB'] * 8,
        body_front=['BBDBBDBB', 'BDBBBBDB', 'DBBPPBBD', 'BBBPPBBB', 'BDBBBBDB', 'BBDBBDBB', 'BBDBBDBB', 'BDBBBBDB',
                    'BBBBBBBB', 'DDDDDDDD', 'BBBkkBBB', 'BBBBBBBB'],
        body_back=['BBDBBDBB', 'BBDBBDBB', 'BDBBBBDB', 'BDBBBBDB', 'BBDBBDBB', 'BBDBBDBB', 'BDBBBBDB', 'BDBBBBDB',
                   'BBBBBBBB', 'DDDDDDDD', 'BBBBBBBB', 'BBBBBBBB'],
        arm=['BDDB', 'BBBB', 'BDBB', 'BBDB', 'BBBB', 'BDBB', 'BBDB', 'BBBB', 'DDDD', 'BBBB', 'bBBb', 'BBBB'],
        leg=['BBBB', 'BDBB', 'BBDB', 'BBBB', 'DDDD', 'BBBB', 'BDBB', 'BBDB', 'BBBB', 'BBBB', 'DDDD', 'BBBB'],
        # Two long rabbit-like ears: addBox(-3.5, -14, -0.5, 1, 6, 1) and (2.5, ...), texture (0, 32)
        extras=lambda p: p.box(0, 32, 1, 6, 1, 'D', 'B', ['D', 'B', 'B', 'B', 'B', 'B'], ['D', 'B', 'b', 'b', 'B', 'B'],
                               ['D', 'B', 'B', 'B', 'B', 'B'], ['D', 'B', 'B', 'B', 'B', 'B']),
    ),
    'magicians_red': dict(
        palette={'R': '#B32D1F', 'O': '#E06A2A', 'o': '#F29A55', 'Y': '#F0C030', 'k': '#141414', 'w': '#F5F5F5',
                 'F': '#7A1A12'},
        base='R',
        head_front=['FRRRRRRF', 'RROOOORR', 'RkwOOwkR', 'ROOYYOOR', 'RROYYORR', 'RRRYYRRR', 'FRRRRRRF', 'FFRRRRFF'],
        head_side=['FFRRRRRR', 'FRRRRROO', 'RRFRRROk', 'RRRFRROO', 'RFRRRROY', 'RRFRRRRR', 'FFRRRRRF', 'FFFRRRFF'],
        head_back=['FRFRFRFR', 'RFRFRFRF', 'FRRRRRRF', 'RFRRRRFR', 'FRFRRFRF', 'RRFRRFRR', 'FRRRRRRF', 'FFRRRRFF'],
        head_top=['RRRFFRRR', 'RRFFFFRR', 'RRRFFRRR', 'RRFFFFRR', 'RRRFFRRR', 'RRFFFFRR', 'RRRFFRRR', 'RRRRRRRR'],
        body_front=['RRRRRRRR', 'ROOOOOOR', 'OoORROoO', 'OOORROOO', 'ROOOOOOR', 'OOROOROO', 'OOROOROO', 'ROOOOOOR',
                    'RRRRRRRR', 'FFFFFFFF', 'RRRYYRRR', 'RRRRRRRR'],
        body_back=['RRRRRRRR', 'RFRRRRFR', 'RRFRRFRR', 'RRRFFRRR', 'RRRFFRRR', 'RRRRRRRR', 'RRFRRFRR', 'RRRRRRRR',
                   'RRRRRRRR', 'FFFFFFFF', 'RRRRRRRR', 'RRRRRRRR'],
        arm=['FRRF', 'RRRR', 'ROOR', 'OOOO', 'OoOO', 'OOOO', 'ROOR', 'RRRR', 'FFFF', 'OOOO', 'oOOo', 'OOOO'],
        leg=['RRRR', 'RRRR', 'RORR', 'RRRR', 'FRRF', 'RRRR', 'RRRR', 'RROR', 'RRRR', 'FFFF', 'YYYY', 'YkYk'],
        # Beak: addBox(-1, -5, -7, 2, 2, 3), texture (0, 32)
        extras=lambda p: p.box(0, 32, 2, 2, 3, 'Y', 'Y', 'Y', ['YY', 'kk'], 'Y', 'Y'),
    ),
    'c_moon': dict(
        palette={'G': '#3F9B45', 'D': '#1C4A22', 'L': '#A6E3A0', 'k': '#141414'},
        base='G',
        head_front=['DDDDDDDD', 'DGGGGGGD', 'GLGGGGLG', 'GkGGGGkG', 'GGGDDGGG', 'GGDGGDGG', 'DGGGGGGD', 'DDGGGGDD'],
        head_side=['DDDDDDDD', 'DDGGGGGG', 'DGGGGGLG', 'DGDGGGkG', 'DGDGGGGG', 'DGGGGGGG', 'DDGGGGGD', 'DDDGGGDD'],
        head_back=['DDDDDDDD', 'DDGGGGDD', 'DGGLLGGD', 'DGLGGGGD', 'DGLGGGGD', 'DGGLLGGD', 'DDGGGGDD', 'DDDDDDDD'],
        head_top=['DDDDDDDD', 'DGGGGGGD', 'DGGLLGGD', 'DGLGGGGD', 'DGLGGGGD', 'DGGLLGGD', 'DGGGGGGD', 'DDDDDDDD'],
        # Arrows pointing down: gravity turned upside down.
        body_front=['GGGGGGGG', 'GDGGGGDG', 'GDGGGGDG', 'DDDGGDDD', 'GDGLLGDG', 'GGGLLGGG', 'GGLLLLGG', 'GGGLLGGG',
                    'GGGGGGGG', 'DDDDDDDD', 'GGGkkGGG', 'GGGGGGGG'],
        body_back=['GGGGGGGG', 'GGGDDGGG', 'GGGDDGGG', 'GGDDDDGG', 'GGGDDGGG', 'GGGGGGGG', 'GGGDDGGG', 'GGDDDDGG',
                   'GGGGGGGG', 'DDDDDDDD', 'GGGGGGGG', 'GGGGGGGG'],
        arm=['DGGD', 'GGGG', 'GDGG', 'GDGG', 'DDDG', 'GDGG', 'GGGG', 'GGGG', 'DDDD', 'GGGG', 'LGGL', 'GGGG'],
        leg=['GGGG', 'GDGG', 'GDGG', 'DDDG', 'GDGG', 'GGGG', 'GGGG', 'GGGG', 'GGGG', 'DDDD', 'GGGG', 'GGGG'],
    ),
    'weather_report': dict(
        palette={'W': '#ECEDEF', 'L': '#C4CAD4', 'B': '#6C83A6', 'b': '#A8C4E3', 'k': '#141414'},
        base='L',
        head_front=['LLLLLLLL', 'LWWWWWWL', 'LBkBBkBL', 'LBWBBWBL', 'LBWBBWBL', 'LBBBBBBL', 'LLWWWWLL', 'LLLLLLLL'],
        head_side=['LLLLLLLL', 'LWWLLLWL', 'LWWWLLBL', 'LLWWLLBL', 'LLLWWLBL', 'LLLLLLBL', 'LLLLLLLL', 'LLLLLLLL'],
        head_back=['LLLLLLLL', 'LWWLLWWL', 'WWWWWWWW', 'LWWLLWWL', 'LLLLLLLL', 'LBBBBBBL', 'LLLLLLLL', 'LLLLLLLL'],
        head_top=['LLLLLLLL', 'LWWLLLLL', 'WWWWLLLL', 'LWWLLWWL', 'LLLLWWWW', 'LLLLLWWL', 'LLLLLLLL', 'LLLLLLLL'],
        body_front=['LLLLLLLL', 'LWWLLWWL', 'WWWWWWWW', 'LWWLLWWL', 'LLLBBLLL', 'LLLbbLLL', 'LWWLLWWL', 'WWWWWWWW',
                    'LLLLLLLL', 'BBBBBBBB', 'LLLkkLLL', 'LLLLLLLL'],
        body_back=['LLLLLLLL', 'LLWWLLLL', 'LWWWWLLL', 'LLWWLLLL', 'LLLLLWWL', 'LLLLWWWW', 'LLLLLWWL', 'LLLLLLLL',
                   'LLLLLLLL', 'BBBBBBBB', 'LLLLLLLL', 'LLLLLLLL'],
        arm=['LWWL', 'WWWW', 'LWWL', 'LLLL', 'LLLL', 'LWLL', 'WWWL', 'LWLL', 'BBBB', 'LLLL', 'WLLW', 'LLLL'],
        leg=['LLLL', 'LWWL', 'WWWW', 'LWWL', 'LLLL', 'LLLL', 'LLWL', 'LWWW', 'LLWL', 'BBBB', 'LLLL', 'LLLL'],
    ),
    'echos': dict(
        palette={'G': '#6BBF3F', 'D': '#2F6A23', 'W': '#F5F5F5', 'P': '#E879A8', 'k': '#141414', 'Y': '#EAD24A'},
        base='G',
        head_front=['DDDDDDDD', 'DGGGGGGD', 'GYYGGYYG', 'GYkGGkYG', 'GGGGGGGG', 'GGkkkkGG', 'DGGGGGGD', 'DDGGGGDD'],
        head_side=['DDDDDDDD', 'DGGGGGGG', 'DGDGGGYY', 'DGDGGGYk', 'DGGGGGGG', 'DGGGGGGk', 'DDGGGGGG', 'DDDGGGGD'],
        head_back=['DDDDDDDD', 'DGDGGDGD', 'DGDGGDGD', 'DGDGGDGD', 'DGGGGGGD', 'DGDGGDGD', 'DDGGGGDD', 'DDDDDDDD'],
        head_top=['DDDDDDDD', 'DGDGGDGD'] * 4,
        body_front=['GGGGGGGG', 'GDGGGGDG', 'GGWWWGGG', 'GGGGWGGG', 'GGGWWGGG', 'GGGGWGGG', 'GGWWWGGG', 'GDGGGGDG',
                    'GGGGGGGG', 'DDDDDDDD', 'GGGPPGGG', 'GGGGGGGG'],
        body_back=['GGGGGGGG', 'GDGGGGDG', 'GGDGGDGG', 'GGGDDGGG', 'GGGDDGGG', 'GGDGGDGG', 'GDGGGGDG', 'GGGGGGGG',
                   'GGGGGGGG', 'DDDDDDDD', 'GGGGGGGG', 'GGGGGGGG'],
        arm=['DGGD', 'GGGG', 'DDDD', 'GGGG', 'GGGG', 'DDDD', 'GGGG', 'PGGP', 'DDDD', 'GGGG', 'WGGW', 'GGGG'],
        leg=['GGGG', 'DDDD', 'GGGG', 'GGGG', 'PPPP', 'GGGG', 'DDDD', 'GGGG', 'GGGG', 'DDDD', 'GGGG', 'GGGG'],
    ),
    'killer_queen': dict(
        palette={'P': '#E07FAE', 'p': '#F4BCD5', 'U': '#5E2E6E', 'k': '#141414', 'W': '#F2F2F2'},
        base='P',
        head_front=['PPPPPPPP', 'PPPPPPPP', 'PUkPPkUP', 'PPPPPPPP', 'PPPUUPPP', 'PPUPPUPP', 'PPPPPPPP', 'UPPPPPPU'],
        head_side=['PPPPPPPP', 'PPUPPPPP', 'PPPUPPPU', 'PPPPPPPP', 'PPPPPPPP', 'PPUPPPPP', 'PPPPPPPP', 'UUPPPPPP'],
        head_back=['PPPPPPPP', 'PPUPPUPP', 'PPPUUPPP', 'PPPPPPPP', 'PPPPPPPP', 'PPPUUPPP', 'PPPPPPPP', 'UUPPPPUU'],
        head_top=['PPPPPPPP', 'PPPUUPPP', 'PPUPPUPP'] + ['PPPPPPPP'] * 5,
        body_front=['PPPPPPPP', 'PpPPPPpP', 'PPUPPUPP', 'PPPPPPPP', 'PPPUUPPP', 'PPPPPPPP', 'PPPPPPPP', 'PPPPPPPP',
                    'UUUUUUUU', 'UUWWWUUU', 'UWkWkWUU', 'UUWWWUUU'],
        body_back=['PPPPPPPP', 'PPUPPUPP', 'PPPUUPPP', 'PPPPPPPP', 'PPPPPPPP', 'PPPUUPPP', 'PPPPPPPP', 'PPPPPPPP',
                   'UUUUUUUU', 'UUUUUUUU', 'PPPPPPPP', 'PPPPPPPP'],
        arm=['PUUP', 'PPPP', 'PPPP', 'PpPP', 'PPPP', 'PPPP', 'PPPP', 'PPPP', 'UUUU', 'PWWP', 'WkkW', 'PWWP'],
        leg=['PPPP', 'PPPP', 'PPPP', 'PPPP', 'UPPU', 'UUUU', 'PPPP', 'PPPP', 'PPPP', 'PPPP', 'UUUU', 'PPPP'],
        # Cat ears: addBox(-4, -10, -1, 2, 2, 1) and (2, ...), texture (0, 32)
        extras=lambda p: p.box(0, 32, 2, 2, 1, 'P', 'P', 'P', ['PP', 'UU'], 'P', 'P'),
    ),
    'king_crimson': dict(
        palette={'R': '#B3202F', 'D': '#6B0E18', 'W': '#EDEDED', 'k': '#141414', 'G': '#4FBF6A'},
        base='R',
        # The small face on the forehead is Epitaph.
        head_front=['RRRRRRRR', 'RRGRRGRR', 'RRRWWRRR', 'RDRRRRDR', 'RkWRRWkR', 'RRRRRRRR', 'RRDDDDRR', 'RRRRRRRR'],
        head_side=['RRRRRRRR', 'RWRRRRRR', 'RRWRRRRR', 'RRRWRRRD', 'RRRRWRRk', 'RRRRRWRR', 'RRRRRRRR', 'DRRRRRRR'],
        head_back=['RWRRRRWR', 'RRWRRWRR', 'RRRWWRRR', 'RRRWWRRR', 'RRWRRWRR', 'RWRRRRWR', 'WRRRRRRW', 'RRRRRRRR'],
        head_top=['RWRRRRWR', 'RRWRRWRR', 'RRRWWRRR', 'RRRWWRRR', 'RRWRRWRR', 'RWRRRRWR', 'WRRRRRRW', 'RRRRRRRR'],
        body_front=['RWRRRRWR', 'RRWRRWRR', 'RRRWWRRR', 'RRRWWRRR', 'RRWRRWRR', 'RWRRRRWR', 'WRRRRRRW', 'RWRRRRWR',
                    'RRWRRWRR', 'DDDDDDDD', 'RRRWWRRR', 'RRRRRRRR'],
        body_back=['WRRRRRRW', 'RWRRRRWR', 'RRWRRWRR', 'RRRWWRRR', 'RRRWWRRR', 'RRWRRWRR', 'RWRRRRWR', 'WRRRRRRW',
                   'RWRRRRWR', 'DDDDDDDD', 'RRRRRRRR', 'RRRRRRRR'],
        arm=['RWWR', 'WRRW', 'RWWR', 'WRRW', 'RWWR', 'WRRW', 'RWWR', 'WRRW', 'DDDD', 'RRRR', 'WRRW', 'RRRR'],
        leg=['WRRW', 'RWWR', 'WRRW', 'RWWR', 'WRRW', 'RWWR', 'WRRW', 'RWWR', 'WRRW', 'DDDD', 'RRRR', 'RRRR'],
    ),
    'green_day': dict(
        palette={'G': '#4A8B37', 'D': '#24461A', 'M': '#9CC26A', 'k': '#141414', 'W': '#F2F2F2'},
        base='G',
        head_front=['DDDDDDDD', 'DGGMGGGD', 'GkGGGGkG', 'GGGGGGGG', 'GWkWkWkG', 'GGGGGGGG', 'DGGMGGGD', 'DDGGGGDD'],
        head_side=['DDDDDDDD', 'DGGGMGGG', 'DGMGGGGk', 'DGGGGGGG', 'DGGGMGGW', 'DGGGGGGG', 'DDGMGGGG', 'DDDGGGGD'],
        head_back=['DDDDDDDD', 'DGMGGMGD', 'DGGGGGGD', 'DGGMGGGD', 'DGGGGMGD', 'DMGGGGGD', 'DDGGGGDD', 'DDDDDDDD'],
        head_top=['DDDDDDDD', 'DGGMGGGD', 'DGGGGMGD', 'DMGGGGGD', 'DGGGMGGD', 'DGMGGGGD', 'DGGGGGMD', 'DDDDDDDD'],
        # Spots of mould.
        body_front=['GGGGGGGG', 'GMGGGDGG', 'GGGMGGGG', 'GDGGGMGG', 'GGGGGGGG', 'GMGGDGGG', 'GGGGGGMG', 'GDGMGGGG',
                    'GGGGGGGG', 'DDDDDDDD', 'GGGkkGGG', 'GGGGGGGG'],
        body_back=['GGGGGGGG', 'GGMGGGDG', 'GGGGMGGG', 'GDGGGGGG', 'GGGMGGMG', 'GGGGGGGG', 'GMGGDGGG', 'GGGGGGGG',
                   'GGGMGGGG', 'DDDDDDDD', 'GGGGGGGG', 'GGGGGGGG'],
        arm=['GMGG', 'GGGG', 'GGDG', 'MGGG', 'GGGM', 'GDGG', 'GGGG', 'GMGG', 'DDDD', 'GGGG', 'MGGM', 'GGGG'],
        leg=['GGGG', 'GMGG', 'GGGG', 'GGDG', 'MGGG', 'GGGM', 'GGGG', 'GDGG', 'GGGG', 'DDDD', 'GGGG', 'GGGG'],
    ),
}


def make_stand(name, s):
    img = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    p = Painter(img, s['palette'], sum(map(ord, name)))
    b = s['base']

    # Head: addBox(-4, -8, -4, 8, 8, 8) at (0, 0)
    p.box(0, 0, 8, 8, 8, s['head_top'], b, s['head_side'], s['head_front'], side_of(s['head_side']), s['head_back'])
    # Body: addBox(-4, 0, -2, 8, 12, 4) at (16, 16)
    p.box(16, 16, 8, 12, 4, b, b, b, s['body_front'], b, s['body_back'])
    # Right arm (the 64x32 format mirrors it for the left one): addBox(-3, -2, -2, 4, 12, 4) at (40, 16)
    arm = s['arm']
    p.box(40, 16, 4, 12, 4, b, arm[-1][0], arm, arm, arm, arm)
    # Right leg: addBox(-2, 0, -2, 4, 12, 4) at (0, 16)
    leg = s['leg']
    p.box(0, 16, 4, 12, 4, b, leg[-1][0], leg, leg, leg, leg)

    if 'extras' in s:
        s['extras'](p)

    img.save(f'{OUT}/stand/{name}.png')
    return img


def front_view(img, back=False):
    """A flat front (or back) view of the skin for the preview sheet."""
    v = Image.new('RGBA', (16, 32), (0, 0, 0, 0))
    if not back:
        parts = [((8, 8, 16, 16), (4, 0)), ((20, 20, 28, 32), (4, 8)), ((44, 20, 48, 32), (0, 8)),
                 ((44, 20, 48, 32), (12, 8)), ((4, 20, 8, 32), (4, 20)), ((4, 20, 8, 32), (8, 20))]
    else:
        parts = [((24, 8, 32, 16), (4, 0)), ((32, 20, 40, 32), (4, 8)), ((52, 20, 56, 32), (0, 8)),
                 ((52, 20, 56, 32), (12, 8)), ((12, 20, 16, 32), (4, 20)), ((12, 20, 16, 32), (8, 20))]
    for (box, pos) in parts:
        v.paste(img.crop(box), pos)
    return v


# --- Sheer Heart Attack: a small tank with a skull on the front, see ModelSheerHeartAttack ---
def make_sheer_heart_attack():
    img = Image.new('RGBA', (64, 32), (0, 0, 0, 0))
    # Body blue with a green tint, white skull, orange-brown knob on the back.
    pal = {'S': '#5B8C99', 's': '#7FAEBA', 'd': '#3F6772', 'T': '#3A3A3E', 't': '#5A5A60', 'W': '#F2EFE6',
           'k': '#141414', 'O': '#C47A32', 'o': '#8E5526'}
    p = Painter(img, pal, 4711)
    # Hull: addBox(-3, 18, -3, 6, 4, 7) at (0, 0)
    p.box(0, 0, 6, 4, 7, 'S', 'd', ['SSSSSSS', 'sSSSSSS', 'SSSSSSS', 'ddddddd'], ['SSSSSS', 'SssssS', 'SSSSSS', 'dddddd'],
          ['SSSSSSS', 'SSSSSSs', 'SSSSSSS', 'ddddddd'], ['SSSSSS', 'SdSSdS', 'SSSSSS', 'dddddd'])
    # Dome: addBox(-2, 16, -2, 4, 2, 5) at (0, 11)
    p.box(0, 11, 4, 2, 5, ['ssss', 'sSSs', 'SSSS', 'SSSS', 'SSSS'], 'S', 'S', ['sSSs', 'SSSS'], 'S', 'S')
    # Skull on the front: addBox(-2, 17, -4, 4, 4, 1) at (26, 0)
    p.box(26, 0, 4, 4, 1, 'W', 'W', 'W', ['WWWW', 'kWWk', 'WWWW', 'WkkW'], 'W', 'W')
    # Tracks: addBox(-4.5, 20, -4, 2, 4, 8) at (26, 8), the other one mirrored
    track_side = ['TtTtTtTt', 'tTTTTTTt', 'tTTTTTTt', 'TtTtTtTt']
    p.box(26, 8, 2, 4, 8, ['TT', 'tt'] * 4, ['TT', 'tt'] * 4, track_side, ['tt', 'TT', 'TT', 'tt'], track_side, ['tt', 'TT', 'TT', 'tt'])
    # Knob on the back: addBox(1.5, 13, 1, 1, 3, 1) at (50, 0)
    p.box(50, 0, 1, 3, 1, 'O', 'o', ['O', 'O', 'o'], ['O', 'O', 'o'], ['O', 'O', 'o'], ['O', 'O', 'o'])
    img.save(f'{OUT}/sheer_heart_attack.png')
    return img


if __name__ == '__main__':
    import os
    os.makedirs(f'{OUT}/stand', exist_ok=True)
    names = list(STANDS)
    sheet = Image.new('RGBA', (len(names) * 180, 400), (40, 44, 52, 255))
    draw = ImageDraw.Draw(sheet)
    for i, name in enumerate(names):
        skin = make_stand(name, STANDS[name])
        sheet.paste(front_view(skin).resize((80, 160), Image.NEAREST), (i * 180 + 5, 30), front_view(skin).resize((80, 160), Image.NEAREST))
        sheet.paste(front_view(skin, True).resize((80, 160), Image.NEAREST), (i * 180 + 92, 30), front_view(skin, True).resize((80, 160), Image.NEAREST))
        draw.text((i * 180 + 8, 8), name.replace('_', ' ').title(), fill=(255, 255, 255, 255))
        sheet.paste(skin.resize((128, 128), Image.NEAREST), (i * 180 + 25, 230))
    sheet.save(f'{PREVIEW}/stands_preview.png')
    make_sheer_heart_attack().resize((256, 128), Image.NEAREST).save(f'{PREVIEW}/sha_texture.png')
    print('ok', names)
