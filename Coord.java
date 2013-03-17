package treebreaker;

class Coord {
	public static final Coord east =  new Coord( 1, 0, 0);
	public static final Coord west =  new Coord(-1, 0, 0);
	public static final Coord north = new Coord( 0, 0,-1);
	public static final Coord south = new Coord( 0, 0, 1);
	public static final Coord up =    new Coord( 0, 1, 0);
	public static final Coord down =  new Coord( 0,-1, 0);
	public static Coord[] sideToCoord = {up, down, south, north, east, west};

	public static final Coord ne = north.addVector(east);
	public static final Coord nw = north.addVector(west);
	public static final Coord se = south.addVector(east);
	public static final Coord sw = south.addVector(west);

	public static final Coord ue = up.addVector(east);
	public static final Coord uw = up.addVector(west);
	public static final Coord de = down.addVector(east);
	public static final Coord dw = down.addVector(west);

	public static final Coord un = up.addVector(north);
	public static final Coord us = up.addVector(south);
	public static final Coord dn = down.addVector(north);
	public static final Coord ds = down.addVector(south);

	public static final Coord une = up.addVector(north.addVector(east));
	public static final Coord unw = up.addVector(north.addVector(west));
	public static final Coord use = up.addVector(south.addVector(east));
	public static final Coord usw = up.addVector(south.addVector(west));
	public static final Coord dne = down.addVector(north.addVector(east));
	public static final Coord dnw = down.addVector(north.addVector(west));
	public static final Coord dse = down.addVector(south.addVector(east));
	public static final Coord dsw = down.addVector(south.addVector(west));

	public int x = 0;
	public int y = 0;
	public int z = 0;

	public Coord(long l, long m, long n) {
		this.x = (int) l;
		this.y = (int) m;
		this.z = (int) n;
	}

	public Coord() {
	}

	public Coord subtract(Coord position) {
		return new Coord(position.x - x, position.y - y, position.z - z);
	}

	public Coord addVector(Coord c) {
		return new Coord(x + c.x, y + c.y, z + c.z);
	}

	public Coord addVector(int x2, int y2, int z2) {
		return new Coord(x2 + x, y2 + y, z2 + z);
	}

	public String toString() {
		return (new StringBuilder()).append("(").append(x).append(", ")
				.append(y).append(", ").append(z).append(")").toString();
	}

	@Override
	public int hashCode() {
		return 13 * 13 * x + 13 * y + z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Coord) {
			Coord pos = (Coord) obj;
			if (x == pos.x && y == pos.y && z == pos.z) {
				return true;
			}
		}
		return false;
	}
}