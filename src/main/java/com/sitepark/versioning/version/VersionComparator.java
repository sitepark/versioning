package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator} for {@link Version}s with default implementations and
 * {@link Builder}.
 *
 * @param <T> the type of {@link Version} to compare
 */
@FunctionalInterface
public interface VersionComparator<T extends Version> extends Comparator<T>, Serializable {

  /**
   * A {@link Comparator} with natual ordering (ascending) that respects all
   * aspects of the {@link Version}s given.
   */
  public static final VersionComparator<Version> NATUAL =
      (a, b) -> {
        int cmp;
        if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
          return cmp;
        }
        if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
          return cmp;
        }
        if ((cmp = VersionComparator.compareBranch(a, b)) != 0) {
          return cmp;
        }
        if ((cmp = VersionComparator.compareQualifiers(a, b)) != 0) {
          return cmp;
        }
        return VersionComparator.compareConcreteSnapshots(a, b);
      };

  /**
   * A {@link Comparator} with reversed ordering (descending) that respects all
   * aspects of the {@link Version}s given.
   */
  public static final VersionComparator<Version> REVERSED =
      (a, b) -> {
        int cmp;
        if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
          return cmp * -1;
        }
        if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
          return cmp * -1;
        }
        if ((cmp = VersionComparator.compareBranch(a, b)) != 0) {
          return cmp * -1;
        }
        if ((cmp = VersionComparator.compareQualifiers(a, b)) != 0) {
          return cmp * -1;
        }
        return VersionComparator.compareConcreteSnapshots(a, b) * -1;
      };

  /**
   * A {@link Comparator} with natual ordering (ascending) that respects all
   * aspects of the {@link Version}s given except {@link Branch}es.
   */
  public static final VersionComparator<Version> IGNORING_BRANCHES =
      VersionComparator.builder().ignoreBranches().build();

  /**
   * A builder to create individual {@link VersionComparator} instances.
   *
   * This should be used when intending to ignore certain aspects of the
   * {@link Version}s; For default implementations see {@link #NATUAL} and
   * {@link #REVERSED}.
   */
  public static final class Builder {
    /**
     * the last 4 bit contain in order:
     * - 1 if concrete snapshots should be ignored
     * - 1 if qualifiers should be ignored
     * - 1 if branches should be ignored
     * - 1 if the order should be descending
     */
    private byte instruction;

    private Builder() {
      /* the default {@code 0} means to not ignore anything and compare in
       * natual order */
      this.instruction = 0b0000;
    }

    /**
     * Reverse the natual ordering.
     *
     * @return itself
     */
    public Builder reverse() {
      this.instruction |= 0b0001;
      return this;
    }

    /**
     * Ignore the {@link Branch}es when comparing {@link Version}s.
     *
     * @return itself
     */
    public Builder ignoreBranches() {
      this.instruction |= 0b0010;
      return this;
    }

    /**
     * Ignore the qualifiers when comparing {@link Version}s.
     *
     * @return itself
     */
    public Builder ignoreQualifiers() {
      this.instruction |= 0b0100;
      return this;
    }

    /**
     * Ignore the timestamp and buildnumber of {@link ConcreteSnapshotVersion}s
     * when comparing.
     *
     * @return itself
     */
    public Builder ignoreConcreteSnapshots() {
      this.instruction |= 0b1000;
      return this;
    }

    /**
     * Build the {@link VersionComparator} from the configured rules.
     *
     * @return the comparator
     */
    public VersionComparator<Version> build() {
      /* this moves the last bit of {@link #instruction} to the most significant
       * bit such that it decides wether or not {@code 1} is negative. */
      final int direction = (this.instruction << (Integer.BYTES - 1)) | 1;
      /* we shift everything to the right such that we do not have to have a
       * case for each variant with each direction */
      return switch (this.instruction >> 1) {
          // include everything
        case 0 -> direction == 1 ? VersionComparator.NATUAL : VersionComparator.REVERSED;
          // exclude branches
        case 1 ->
            (a, b) -> {
              int cmp;
              if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareQualifiers(a, b)) != 0) {
                return cmp * direction;
              }
              return VersionComparator.compareConcreteSnapshots(a, b) * direction;
            };
          // exclude qualifiers
        case 2 ->
            (a, b) -> {
              int cmp;
              if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareBranch(a, b)) != 0) {
                return cmp * direction;
              }
              return VersionComparator.compareConcreteSnapshots(a, b) * direction;
            };
          // exclude branches and qualifiers
        case 3 ->
            (a, b) -> {
              int cmp;
              if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
                return cmp * direction;
              }
              return VersionComparator.compareConcreteSnapshots(a, b) * direction;
            };
          // exclude concrete snapshots
        case 4 ->
            (a, b) -> {
              int cmp;
              if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareBranch(a, b)) != 0) {
                return cmp * direction;
              }
              return VersionComparator.compareQualifiers(a, b) * direction;
            };
          // exclude branches and concrete snapshots
        case 5 ->
            (a, b) -> {
              int cmp;
              if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
                return cmp * direction;
              }
              return VersionComparator.compareQualifiers(a, b) * direction;
            };
          // exclude qualifiers and concrete snapshots
        case 6 ->
            (a, b) -> {
              int cmp;
              if ((cmp = VersionComparator.compareNumbers(a, b)) != 0) {
                return cmp * direction;
              }
              if ((cmp = VersionComparator.compareSnapshots(a, b)) != 0) {
                return cmp * direction;
              }
              return VersionComparator.compareBranch(a, b) * direction;
            };
          // exclude branches, qualifiers and concrete snapshots
        case 7 ->
            (a, b) -> {
              final int cmp = VersionComparator.compareNumbers(a, b);
              return (cmp == 0 ? VersionComparator.compareSnapshots(a, b) : cmp) * direction;
            };
          // something went wrong
        default ->
            throw new IllegalStateException("illegal internal state (" + this.instruction + ")");
      };
    }
  }

  /**
   * Create a {@link Builder} to build an individual {@link VersionComparator}
   * instance.
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  private static int compareNumbers(final Version a, final Version b) {
    int cmp;
    if ((cmp = a.getMajor() - b.getMajor()) != 0) {
      return cmp;
    }
    if ((cmp = a.getMinor() - b.getMinor()) != 0) {
      return cmp;
    }
    return a.getIncremental() - b.getIncremental();
  }

  private static int compareBranch(final Version a, final Version b) {
    return a.getBranch().compareTo(b.getBranch());
  }

  private static int compareSnapshots(final Version a, final Version b) {
    // Snapshot < Release
    return (a.isSnapshot() ? -1 : 1) - (b.isSnapshot() ? -1 : 1);
  }

  private static int compareQualifiers(final Version a, final Version b) {
    int cmp = 0;
    for (int i = 0; cmp == 0; i++) {
      if (i == a.getQualifiers().size()) {
        return i == b.getQualifiers().size() ? cmp : 1;
      }
      if (i == b.getQualifiers().size()) {
        return -1; // more qualifiers => smaller value
      }
      cmp = a.getQualifiers().get(i).compareTo(b.getQualifiers().get(i));
    }
    return cmp;
  }

  private static int compareConcreteSnapshots(final Version a, final Version b) {
    if (a instanceof final ConcreteSnapshotVersion ca
        && b instanceof final ConcreteSnapshotVersion cb) {
      final int cmp = ca.getTimestamp().compareTo(cb.getTimestamp());
      return cmp == 0 ? ca.getBuildnumber() - cb.getBuildnumber() : cmp;
    }
    return 0;
  }
}
