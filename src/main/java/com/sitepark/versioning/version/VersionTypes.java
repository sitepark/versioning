package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a subset of the following {@link Type}s {@link Version}s can be
 * divided into:
 * <ul>
 *   <li>{@link PublicationStatusType#RELEASES}</li>
 *   <li>{@link PublicationStatusType#SNAPSHOTS}</li>
 *   <li>{@link BranchType#DEVELOP}</li>
 *   <li>{@link BranchType#FEATURES}</li>
 * </ul>
 * A VersionTypes instance may include any number of these.
 *
 * <p>
 * This class is inteded to be used for APIs that permit a user to specify
 * multiple properties of Versions without having to have fields for each
 * one.
 *
 * <p>
 * If used non-programatically one may prefere to use one of defined constants.
 */
public final class VersionTypes {

  /**
   * A superclass for all Types that may be used in {@link VersionTypes}.
   * This is not intended to be inherited!
   *
   * @see PublicationStatusType
   * @see BranchType
   */
  public abstract static class Type {
    /** a bitmask, that has to be unique for each subclass! */
    private final byte bitmask;

    private final String name;

    /**
     * To prevent non-unique bitmasks Types may not have public
     * constructors.
     */
    private Type(final String name, final int bitmask) {
      this.name = name;
      this.bitmask = (byte) bitmask;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  /**
   * A {@link Type} of {@link VersionTypes} that distinguishes
   * {@link Version}s by their state of publication.
   * A {@code Version} will always fall into exactly one of these categories:
   * <ul>
   *   <li>{@link PublicationStatusType#RELEASES}</li>
   *   <li>{@link PublicationStatusType#SNAPSHOTS}</li>
   * </ul>
   *
   * @see Version#isSnapshot()
   */
  public static final class PublicationStatusType extends Type {
    /**
     * Represents {@link ReleaseVersion}s in {@link VersionTypes}.
     */
    public static final PublicationStatusType RELEASES =
        new PublicationStatusType("releases", 0b0000_0001);

    /**
     * Represents {@link SnapshotVersion}s and
     * {@link ConcreteSnapshotVersion}s in {@link VersionTypes}.
     */
    public static final PublicationStatusType SNAPSHOTS =
        new PublicationStatusType("snapshots", 0b0000_0010);

    /**
     * @see Type#Type(String, int)
     */
    private PublicationStatusType(final String name, int bitmask) {
      super(name, bitmask);
    }

    /**
     * Returns an array of all instances, which are:
     * <ul>
     *   <li>{@link PublicationStatusType#RELEASES}</li>
     *   <li>{@link PublicationStatusType#SNAPSHOTS}</li>
     * </ul>
     *
     * @return all {@code PublicationStatusType} instances
     */
    public static PublicationStatusType[] values() {
      return new PublicationStatusType[] {
        PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS
      };
    }
  }

  /**
   * A {@link Type} of {@link VersionTypes} that distinguishes
   * {@link Version}s by their {@link Branch}.
   * A {@code Version} will always fall into exactly one of these categories:
   * <ul>
   *   <li>{@link BranchType#DEVELOP}</li>
   *   <li>{@link BranchType#FEATURES}</li>
   * </ul>
   *
   * @see Version#getBranch
   * @see Branch#isDevelop()
   * @see Branch#isFeature()
   */
  public static final class BranchType extends Type {
    /**
     * Represents {@link Version}s with a "non-feature-branch" in
     * {@link VersionTypes}.
     *
     * @see Branch#isDevelop()
     */
    public static final BranchType DEVELOP = new BranchType("develop", 0b0000_0100);

    /**
     * Represents {@link Version}s with a "feature-branch" in
     * {@link VersionTypes}.
     *
     * @see Branch#isFeature()
     */
    public static final BranchType FEATURES = new BranchType("features", 0b0000_1000);

    /**
     * @see Type#Type(String, int)
     */
    private BranchType(final String name, int value) {
      super(name, value);
    }

    /**
     * Returns an array of all instances, which are:
     * <ul>
     *   <li>{@link BranchType#DEVELOP}</li>
     *   <li>{@link BranchType#FEATURES}</li>
     * </ul>
     *
     * @return all {@code BranchType} instances
     */
    public static BranchType[] values() {
      return new BranchType[] {BranchType.DEVELOP, BranchType.FEATURES};
    }
  }

  /**
   * Instance without any {@link Type}s.
   */
  public static final VersionTypes NONE = new VersionTypes((byte) 0b0000_0000);

  /**
   * Instance that includes exactly {@link BranchType#DEVELOP}
   * and {@link PublicationStatusType#RELEASES}.
   */
  public static final VersionTypes ONLY_DEVELOP_RELEASES =
      new VersionTypes(BranchType.DEVELOP, PublicationStatusType.RELEASES);

  /**
   * Instance that includes exactly {@link BranchType#DEVELOP}
   * and {@link PublicationStatusType#SNAPSHOTS}.
   */
  public static final VersionTypes ONLY_DEVELOP_SNAPSHOTS =
      new VersionTypes(BranchType.DEVELOP, PublicationStatusType.SNAPSHOTS);

  /**
   * Instance that includes exactly {@link BranchType#DEVELOP},
   * {@link PublicationStatusType#RELEASES} and
   * {@link PublicationStatusType#SNAPSHOTS}.
   * This excludes only {@link BranchType#FEATURES}.
   */
  public static final VersionTypes DEVELOP_RELEASES_AND_SNAPSHOTS =
      new VersionTypes(
          BranchType.DEVELOP, PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS);

  /**
   * Instance that includes all {@link Type}s.
   */
  public static final VersionTypes ALL = new VersionTypes((byte) 0b1111_1111);

  /**
   * Each bit of this value represents the presence (if {@code 1}) or absense
   * (if {@code 0}) of a {@link Type} with the corresponding
   * {@link Type#bitmask}.
   */
  private final byte value;

  /**
   * Class Constructor specifiying all included {@link Type}s as
   * {@code Iterable}.
   *
   * @param types an {@code Iterable} containing all {@code Type}s to include
   * @see VersionTypes#includes(Type)
   */
  public VersionTypes(final Iterable<Type> types) {
    byte value = 0;
    for (final Type type : types) {
      value |= type.bitmask;
    }
    this.value = value;
  }

  /**
   * Class Constructor specifiying all included {@link Type}s as varargs
   * array.
   *
   * @param types the types to be included
   * @see VersionTypes#includes(Type)
   */
  public VersionTypes(final Type... types) {
    byte value = 0;
    for (final VersionTypes.Type type : types) {
      value |= type.bitmask;
    }
    this.value = value;
  }

  /**
   * Class Constructor specifiying all included {@link Type}s as single
   * {@code byte} value.
   * Exists for the creation of constants to be cheaper.
   *
   * @param value a {@code byte} value representing the Types to be included
   * @see VersionTypes#includes(Type)
   */
  private VersionTypes(final byte value) {
    this.value = value;
  }

  /**
   * Returns wether the given {@link Type} has been specified to be included
   * in this instance.
   *
   * @param type the {@code Type} to check for inclusion
   * @return {@code true} if the Type is included, {@code false} otherwise
   */
  public boolean includes(final Type type) {
    return (this.value & type.bitmask) == type.bitmask;
  }

  /**
   * Returns a {@link Set} of all {@link PublicationStatusType}s included in
   * this instance.
   *
   * @return a {@code Set} containing all {@code PublicationStatusType}s
   *         included in this instance
   * @see VersionTypes#includes(Type)
   * @see VersionTypes#getBranchTypes()
   * @see VersionTypes#getTypes()
   */
  public Set<PublicationStatusType> getPublicationStatusTypes() {
    switch (this.value & 0b0000_0011) {
      case 0b0000_0001:
        return Set.of(PublicationStatusType.RELEASES);
      case 0b0000_0010:
        return Set.of(PublicationStatusType.SNAPSHOTS);
      case 0b0000_0011:
        return Set.of(PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS);
      default:
        return Set.of();
    }
  }

  /**
   * Returns a {@link Set} of all {@link BranchType}s included in this
   * instance.
   *
   * @return a {@code Set} containing all {@code BranchType}s included in this
   *         instance
   * @see VersionTypes#includes(Type)
   * @see VersionTypes#getPublicationStatusTypes()
   * @see VersionTypes#getTypes()
   */
  public Set<BranchType> getBranchTypes() {
    switch (this.value & 0b0000_1100) {
      case 0b0000_0100:
        return Set.of(BranchType.DEVELOP);
      case 0b0000_1000:
        return Set.of(BranchType.FEATURES);
      case 0b0000_1100:
        return Set.of(BranchType.DEVELOP, BranchType.FEATURES);
      default:
        return Set.of();
    }
  }

  /**
   * Returns a {@link Set} of all {@link Type}s included in this instance.
   *
   * @return a {@code Set} containing all {@code Type}s included in this
   *         instance
   * @see VersionTypes#includes(Type)
   * @see VersionTypes#getPublicationStatusTypes()
   * @see VersionTypes#getBranchTypes()
   */
  public Set<Type> getTypes() {
    switch (this.value & 0b0000_1111) {
      case 0b0000_0001:
        return Set.of(PublicationStatusType.RELEASES);
      case 0b0000_0010:
        return Set.of(PublicationStatusType.SNAPSHOTS);
      case 0b0000_0011:
        return Set.of(PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS);
      case 0b0000_0100:
        return Set.of(BranchType.DEVELOP);
      case 0b0000_0101:
        return Set.of(PublicationStatusType.RELEASES, BranchType.DEVELOP);
      case 0b0000_0110:
        return Set.of(PublicationStatusType.SNAPSHOTS, BranchType.DEVELOP);
      case 0b0000_0111:
        return Set.of(
            PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS, BranchType.DEVELOP);
      case 0b0000_1000:
        return Set.of(BranchType.FEATURES);
      case 0b0000_1001:
        return Set.of(PublicationStatusType.RELEASES, BranchType.FEATURES);
      case 0b0000_1010:
        return Set.of(PublicationStatusType.SNAPSHOTS, BranchType.FEATURES);
      case 0b0000_1011:
        return Set.of(
            PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS, BranchType.FEATURES);
      case 0b0000_1100:
        return Set.of(BranchType.DEVELOP, BranchType.FEATURES);
      case 0b0000_1101:
        return Set.of(PublicationStatusType.RELEASES, BranchType.DEVELOP, BranchType.FEATURES);
      case 0b0000_1110:
        return Set.of(PublicationStatusType.SNAPSHOTS, BranchType.DEVELOP, BranchType.FEATURES);
      case 0b0000_1111:
        return Set.of(
            PublicationStatusType.RELEASES,
            PublicationStatusType.SNAPSHOTS,
            BranchType.DEVELOP,
            BranchType.FEATURES);
      default:
        return Set.of();
    }
  }

  @Override
  public String toString() {
    return String.format(
        "%s[publicationStatusTypes=[%s], branchTypes=[%s]]",
        this.getClass().getSimpleName(),
        this.joinTypes(this.getPublicationStatusTypes()),
        this.joinTypes(this.getBranchTypes()));
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof VersionTypes && this.value == ((VersionTypes) other).value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  private String joinTypes(final Set<? extends Type> types) {
    if (types.isEmpty()) {
      return "";
    }
    final Iterator<? extends Type> iterator = types.iterator();
    final StringBuilder stringBuilder = new StringBuilder(iterator.next().toString());
    while (iterator.hasNext()) {
      stringBuilder.append(',');
      stringBuilder.append(' ');
      stringBuilder.append(iterator.next().toString());
    }
    return stringBuilder.toString();
  }
}
