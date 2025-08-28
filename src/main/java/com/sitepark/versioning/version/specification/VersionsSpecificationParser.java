package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.VersionParser;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.SpecificationElement;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import com.sitepark.versioning.version.specification.element.boundary.Boundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InvalidBoundariesException;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;
import java.text.ParseException;

/**
 * A class to parse Strings of a certain format into
 * {@link VersionsSpecification}s. Generally this format is similar to
 * <a href="https://cwiki.apache.org/confluence/display/MAVENOLD/Dependency+Mediation+and+Conflict+Resolution#DependencyMediationandConflictResolution-DependencyVersionRanges">Maven's Version Range Syntax</a>
 * but differs in the following aspects:
 * <ul>
 *   <li>
 *     <code>1.0</code> is not a "Soft" requirement; It means <em>exactly</em>
 *     <code>1.0.0</code>.
 *   </li>
 *   <li><code>[1.0]</code> is invalid syntax.</li>
 *   <li>
 *     {@link Version}s are sensitive to {@link Branch}es.  {@code Version}s in
 *     a {@link VersionRangeElement} may define a {@code Branch} (all the same),
 *     which then implies that only {@code Version}s with this {@code Branch}
 *     are included.  The absence of a {@code Branch} and {@code "develop"} are
 *     considered equal.
 *   </li>
 * </ul>
 *
 * <p>
 * To be exact the format is as follows: {@code "<element>[, <element>]..."}.
 * Each {@link SpecificationElement} should be either a
 * {@link ExplicitVersionElement} or a {@link VersionRangeElement}.
 * {@code ExplicitVersionElement}s consist of a {@link Version}, where as
 * {@code VersionRangeElement}s follow their own format:
 * {@code "<lower-boundary>,<upper-boundary>"}.
 *
 * <table border="1">
 *   <caption>Possible {@link Boundary}s</caption>
 *   <tr>
 *     <th>Name</th>
 *     <th>Syntax</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@link ExclusiveLowerBoundary}</td>
 *     <td>{@code "(<version>"}</td>
 *     <td>{@code x > version}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link InclusiveLowerBoundary}</td>
 *     <td>{@code "[<version>"}</td>
 *     <td>{@code x >= version}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UnlimitedLowerBoundary}</td>
 *     <td>{@code "("}</td>
 *     <td>any {@code x}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link ExclusiveUpperBoundary}</td>
 *     <td>{@code "<version>)"}</td>
 *     <td>{@code x < version}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link InclusiveUpperBoundary}</td>
 *     <td>{@code "<version>]"}</td>
 *     <td>{@code x <= version}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UnlimitedUpperBoundary}</td>
 *     <td>{@code ")"}</td>
 *     <td>any {@code x}</td>
 *   </tr>
 * </table>
 *
 * <p>
 * Each {@code Version} in a {@code VersionsSpecification}-String may be
 * written in any way the {@link VersionParser} understands.  Spaces are
 * ignored (except inside of {@code Version}s).  {@code SpecificationElement}s
 * may not "overlap" and a {@code VersionRangeElement}s {@code Boundary}s cannot
 * both be <em>"unlimitted"</em>.
 *
 * <table border="1">
 *   <caption>Examples</caption>
 *   <tr>
 *     <th>String</th>
 *     <th>Explaination</th>
 *   </tr>
 *   <tr>
 *     <td>{@code 1.0}</td>
 *     <td>{@code x == 1.0.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code (, 1.0]}</td>
 *     <td>{@code x <= 1.0.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code [1.2, 1.3]}</td>
 *     <td>{@code 1.2.0 <= x <= 1.3.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code [1.0, 2.0)}</td>
 *     <td>{@code 1.0.0 <= x < 2.0.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code [1.5, )}</td>
 *     <td>{@code x >= 1.5.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code (, 1.0], [1.2,)}</td>
 *     <td>{@code x <= 1.0.0 or x >= 1.2.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code (, 1.1), (1.1, )}</td>
 *     <td>{@code x != 1.1.0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code [1.0-feature, )}</td>
 *     <td>{@code x >= 1.0.0} of the branch "feature"</td>
 *   </tr>
 *   <tr>
 *     <td>{@code [1.0-feature, 2.0-feature)}</td>
 *     <td>{@code 1.0.0 <= x < 2.0.0} of the branch "feature"</td>
 *   </tr>
 *   <tr>
 *     <td>{@code (, 1.2), 1.2-feature}</td>
 *     <td>{@code x < 1.2.0} or {@code x == 1.2.0-feature}</td>
 *   </tr>
 * </table>
 *
 * <p>
 * This class is immutable and thread-safe.
 *
 * @see #parse(String)
 * @see VersionsSpecificationBuilder
 */
public class VersionsSpecificationParser {

  /**
   * A default instance
   */
  public static final VersionsSpecificationParser DEFAULT_PARSER =
      new VersionsSpecificationParser();

  /**
   * Class Constructor
   */
  public VersionsSpecificationParser() {}

  /**
   * Parses a String into a {@link VersionsSpecification}.
   *
   * The required format is as follows: {@code "<element>[, <element>]..."}.
   * Each {@link SpecificationElement} should be either a
   * {@link ExplicitVersionElement} or a {@link VersionRangeElement}.
   * {@code ExplicitVersionElement}s consist of a {@link Version}, where as
   * {@code VersionRangeElement}s follow their own format:
   * {@code "<lower-boundary>,<upper-boundary>"}.
   * <ul>
   *   <li>
   *     {@link ExclusiveLowerBoundary}: {@code "(<version>"}
   *   </li>
   *   <li>
   *     {@link InclusiveLowerBoundary}: {@code "[<version>"}
   *   </li>
   *   <li>
   *     {@link UnlimitedLowerBoundary}: {@code "("}
   *   </li>
   *   <li>
   *     {@link ExclusiveUpperBoundary}: {@code "<version>)"}
   *   </li>
   *   <li>
   *     {@link InclusiveUpperBoundary}: {@code "<version>]"}
   *   </li>
   *   <li>
   *     {@link UnlimitedUpperBoundary}: {@code ")"}
   *   </li>
   * </ul>
   *
   * <p>
   * Each {@code Version} in a {@code VersionsSpecification}-String may be
   * written in any way the {@link VersionParser} understands.  Spaces are
   * ignored (except inside of {@code Version}s).
   * {@code SpecificationElement}s may not "overlap" and a
   * {@code VersionRangeElement}s {@code Boundary}s cannot both be
   * <em>"unlimitted"</em>.
   *
   * @param string the String to parse
   * @return the parsed {@code VersionsSpecification}
   * @throws ParseException if the String is invalidly formatted
   * @throws InvalidBoundariesException if the boundaries of a
   *                                    {@link VersionRangeElement} are both
   *                                    "unlimited" or if a {@code Version}
   *                                    of a lower {@code Boundary} is
   *                                    greater than a upper
   *                                    {@code Boundary}'s {@code Version}
   */
  public VersionsSpecification parse(final String string)
      throws ParseException, InvalidBoundariesException {
    return new VersionsSpecificationParseExecutor(string).execute();
  }
}
