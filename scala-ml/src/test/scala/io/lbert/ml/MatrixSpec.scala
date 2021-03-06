package io.lbert.ml

import io.lbert.ml.Matrix._
import org.scalatest.{Matchers, WordSpec}

class MatrixSpec extends WordSpec with Matchers {

  "empty" should {
    "create empty matrix" in {
      Matrix.empty[Int] shouldBe Matrix(Seq.empty[Seq[Int]])
    }
  }

  "dimension" should {
    "get dimension of 0 element matrix" in {
      Matrix.dimension(Matrix(Seq.empty[Seq[Int]])) shouldBe 0
    }
    "get dimension of 5x5 matrix" in {
      val m = Matrix.fill(MatrixSize(Row(5),Column(5)),1)
      Matrix.dimension(m) shouldBe 25
    }
  }

  "size" should {
    "get size of 0 element matrix" in {
      Matrix.size(Matrix.empty[Int]) shouldBe MatrixSize(Row(0), Column(0))
    }
    "get size of 5x4 element matrix" in {
      Matrix.size(Matrix.fill(MatrixSize(Row(5), Column(4)),1)) shouldBe MatrixSize(Row(5), Column(4))
    }
  }

  "rows" should {
    "return no rows in empty matrix" in {
      Matrix.rows(Matrix.empty[Int]) shouldBe Row(0)
    }
    "return 5 rows in 5x4 matrix" in {
      Matrix.rows(Matrix.fill(MatrixSize(Row(5), Column(4)),1)) shouldBe Row(5)
    }
  }

  "columns" should {
    "columns no columns in empty matrix" in {
      Matrix.columns(Matrix.empty[Int]) shouldBe Column(0)
    }
    "return 4 columns in 5x4 matrix" in {
      Matrix.columns(Matrix.fill(MatrixSize(Row(5), Column(4)),1)) shouldBe Column(4)
    }
  }

  "fill" should {
    "fill 0 element matrix" in {
      Matrix.fill(MatrixSize(Row(0),Column(0)),1) shouldBe Matrix(
        Seq.empty[Seq[Int]]
      )
    }
    "fill 3x3 element matrix" in {
      Matrix.fill(MatrixSize(Row(3),Column(3)),1) shouldBe Matrix(
        Seq(
          Seq(1,1,1),
          Seq(1,1,1),
          Seq(1,1,1)
        )
      )
    }
  }

  "fillFunc" should {
    "fill 3x3 element matrix with index func" in {
      Matrix.fillFunc[Int](MatrixSize(Row(3),Column(3)),mi => mi.row.underlying * mi.column.underlying) shouldBe Matrix(
        Seq(
          Seq(1,2,3),
          Seq(2,4,6),
          Seq(3,6,9)
        )
      )
    }
  }

  "get" should {
    val twoByTwoMatrix = Matrix.fillFunc(MatrixSize(Row(2),Column(2)),mi => mi.row.underlying * mi.column.underlying)
    "fail if index doesn't exist" in {
      Matrix.get(twoByTwoMatrix, MatrixIndex(Row(3), Column(1))) shouldBe None
      Matrix.get(twoByTwoMatrix, MatrixIndex(Row(1), Column(3))) shouldBe None
    }
    "fetch dimension 1x1 element" in {
      Matrix.get(twoByTwoMatrix, MatrixIndex(Row(1), Column(1))) shouldBe Some(1)
    }
    "fetch dimension 2x2" in {
      Matrix.get(twoByTwoMatrix, MatrixIndex(Row(2), Column(2))) shouldBe Some(4)
    }
  }

  "getIndexes" should {
    "get 4 indexes for a 2x2 matrix" in {
      val twoByTwoMatrix = Matrix.fill(MatrixSize(Row(2),Column(2)),1)
      Matrix.getIndexes(twoByTwoMatrix) shouldBe Seq(
        MatrixIndex(Row(1), Column(1)),
        MatrixIndex(Row(1), Column(2)),
        MatrixIndex(Row(2), Column(1)),
        MatrixIndex(Row(2), Column(2))
      )
    }
  }

  "map" should {
    "apply function to each element" in {
      Matrix.map(Matrix.fill(MatrixSize(Row(2),Column(2)),1))(_ + "hi") shouldBe
        Matrix.fill(MatrixSize(Row(2),Column(2)),"1hi")
    }
  }

  "add" should {
    "fail for matrices that aren't the same size" in {
      val matrix1 = Matrix.fill(MatrixSize(Row(2), Column(2)), 1)
      val matrix2 = Matrix.fill(MatrixSize(Row(3), Column(2)), 2)
      Matrix.add(matrix1, matrix2) shouldBe Left(DifferentSizeError(matrix1, matrix2))
    }
    "add matrices" in {
      Matrix.add(
        Matrix.fill(MatrixSize(Row(3),Column(2)),1),
        Matrix.fill(MatrixSize(Row(3),Column(2)),2)
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),3))
    }
    "add scalar value" in {
      Matrix.add(
        Matrix.fill(MatrixSize(Row(3),Column(2)),1),3
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),4))
    }
  }

  "subtract" should {
    "subtract matrices" in {
      Matrix.subtract(
        Matrix.fill(MatrixSize(Row(3),Column(2)),1),
        Matrix.fill(MatrixSize(Row(3),Column(2)),2)
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),-1))
    }
    "subtract from scalar value" in {
      Matrix.subtract(
        Matrix.fill(MatrixSize(Row(3),Column(2)),10),3
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),7))
    }
    "subtract scalar value from matrix" in {
      Matrix.subtract(
        3, Matrix.fill(MatrixSize(Row(3),Column(2)),10)
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),-7))
    }
  }

  "scalar multiply" should {
    "multiply by scalar value" in {
      Matrix.multiply(
        Matrix.fill(MatrixSize(Row(3),Column(2)),2), 3
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),6))
    }
  }

  "scalar divide" should {
    "fail if 0" in {
      Matrix.divide(
        Matrix.fill(MatrixSize(Row(3),Column(2)),2.0), 0.0
      ) shouldBe Left(DivideByZeroError)
    }
    "divide by scalar value" in {
      Matrix.divide(
        Matrix.fill(MatrixSize(Row(3),Column(2)),10.0), 2.0
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3),Column(2)),5.0))
    }
  }

  "slice rows" should {
    "give back rows" in {
      Matrix.sliceRows(
        Matrix(
          Seq(
            Seq(1,2),
            Seq(3,4),
            Seq(5,6)
          )
        )
      ) shouldBe Seq(
        Seq(1,2),
        Seq(3,4),
        Seq(5,6)
      )
    }
  }

  "slice columns" should {
    "give back columns" in {
      Matrix.sliceColumns(
        Matrix(
          Seq(
            Seq(1,2),
            Seq(3,4),
            Seq(5,6)
          )
        )
      ) shouldBe Seq(
        Seq(1,3,5),
        Seq(2,4,6)
      )
    }
  }

  "multiply" should {
    "fail if matrix isn't the right size" in {
      val matrix1 = Matrix.fill(MatrixSize(Row(3), Column(2)), 1)
      val matrix2 = Matrix.fill(MatrixSize(Row(5), Column(5)), 2)
      Matrix.multiply(matrix1, matrix2) shouldBe Left(ColumnSizeNotRowSizeError(matrix1, matrix2))
    }
    "succeed" in {
      Matrix.multiply(
        Matrix[Double](
          Seq(
            Seq(1,2),
            Seq(3,4),
            Seq(5,6)
          )
        ),
        Matrix[Double](
          Seq(
            Seq(1,2),
            Seq(3,4)
          )
        )
      ) shouldBe Right(
        Matrix[Double](
          Seq(
            Seq(7,10),
            Seq(15,22),
            Seq(23,34)
          )
        )
      )
    }
  }

  "identity" should {
    "create a 5x5 identity matrix" in {
      Matrix.identity[Int](SquareMatrixSize(5)) shouldBe Matrix(
        Seq(
          Seq(1,0,0,0,0),
          Seq(0,1,0,0,0),
          Seq(0,0,1,0,0),
          Seq(0,0,0,1,0),
          Seq(0,0,0,0,1)
        )
      )
    }
  }

  "transpose" should {
    "transpose matrix" in {
      Matrix.transpose(
        Matrix(
          Seq(
            Seq(1,2),
            Seq(3,4),
            Seq(5,6)
          )
        )
      ) shouldBe Matrix[Double](
        Seq(
          Seq(1,3,5),
          Seq(2,4,6)
        )
      )
    }
  }

  "inverse" should {
    "fail if not square matrix" in {
      val matrix = Matrix.fill(MatrixSize(Row(3), Column(2)), 1.0)
      Matrix.inverse(matrix) shouldBe Left(NotSquareError(matrix))
    }
    "fail if matrix isn't inversable" in {
      val matrix = Matrix[Double](
        Seq(
          Seq(3, 4),
          Seq(6, 8)
        )
      )
      Matrix.inverse(matrix) shouldBe Left(InverseUndefinedError(matrix))
    }
    "get inverse matrix" in {
      Matrix.inverse(
        Matrix[Double](
          Seq(
            Seq(1,3,3),
            Seq(1,4,3),
            Seq(1,3,4)
          )
        )
      ) shouldBe Right(Matrix[Double](
        Seq(
          Seq(7,-3,-3),
          Seq(-1,1,0),
          Seq(-1,0,1)
        )
      ))
    }
  }

  "determinant" should {
    "fail if not square matrix" in {
      val matrix = Matrix.fill(MatrixSize(Row(3), Column(2)), 1)
      Matrix.determinant(matrix) shouldBe Left(NotSquareError(matrix))
    }
    "get determinate of 1x1 matrix" in {
      Matrix.determinant(
        Matrix(
          Seq(
            Seq(9)
          )
        )
      ) shouldBe Right(9)
    }
    "get determinate of 2x2 matrix" in {
      Matrix.determinant(
        Matrix(
          Seq(
            Seq(1,4),
            Seq(-1,9)
          )
        )
      ) shouldBe Right(13)
    }
    "get determinate of 3x3 matrix" in {
      Matrix.determinant(
        Matrix(
          Seq(
            Seq(3,2,1),
            Seq(2,1,-3),
            Seq(4,0,1)
          )
        )
      ) shouldBe Right(-29)
    }
    "get determinate of 4x4 matrix" in {
      Matrix.determinant(
        Matrix(
          Seq(
            Seq(3,2,-1,4),
            Seq(2,1,5,7),
            Seq(0,5,2,-6),
            Seq(-1,2,1,0)
          )
        )
      ) shouldBe Right(-418)
    }
  }

  "removeIndexRowColumn" should {
    val threeByThree = Matrix(
      Seq(
        Seq(1,2,3),
        Seq(4,5,6),
        Seq(7,8,9)
      )
    )
    "get row 1 col 2 minor of 3x3" in {
      Matrix.removeRowColumn(
        threeByThree,
        MatrixIndex(Row(1), Column(2))
      ) shouldBe Right(Matrix(
        Seq(
          Seq(4,6),
          Seq(7,9)
        )
      ))
    }
    "get row 1 col 1 minor of 3x3" in {
      Matrix.removeRowColumn(
        threeByThree,
        MatrixIndex(Row(1), Column(1))
      ) shouldBe Right(Matrix(
        Seq(
          Seq(5,6),
          Seq(8,9)
        )
      ))
    }
    "get row 3 col 3 minor of 3x3" in {
      Matrix.removeRowColumn(
        threeByThree,
        MatrixIndex(Row(3), Column(3))
      ) shouldBe Right(Matrix(
        Seq(
          Seq(1,2),
          Seq(4,5)
        )
      ))
    }
    "get row 2 col 2 minor of 3x3" in {
      Matrix.removeRowColumn(
        threeByThree,
        MatrixIndex(Row(2), Column(2))
      ) shouldBe Right(Matrix(
        Seq(
          Seq(1,3),
          Seq(7,9)
        )
      ))
    }
  }

  "scalarPower" should {
    "get power of 5" in {
      Matrix.scalarPower(5,5) shouldBe 3125
    }

  }

  "power" should {
    "get power of 3x3 matrix" in {
      Matrix.power(
        Matrix.fill(MatrixSize(Row(3), Column(2)), 2), 3
      ) shouldBe Right(Matrix.fill(MatrixSize(Row(3), Column(2)), 8))
    }
  }

  "minor" should {
    "get minor of 2x2" in {
      val matrix = Matrix(
        Seq(
          Seq(2,5),
          Seq(4,3)
        )
      )
      Matrix.minor(matrix, MatrixIndex(Row(1), Column(1))) shouldBe Right(3)
      Matrix.minor(matrix, MatrixIndex(Row(1), Column(2))) shouldBe Right(4)
      Matrix.minor(matrix, MatrixIndex(Row(2), Column(1))) shouldBe Right(5)
      Matrix.minor(matrix, MatrixIndex(Row(2), Column(2))) shouldBe Right(2)
    }
    "get minor of 3x3" in {
      val matrix = Matrix(
        Seq(
          Seq(2,3,4),
          Seq(-1,5,1),
          Seq(5,0,3)
        )
      )
      Matrix.minor(matrix, MatrixIndex(Row(1), Column(1))) shouldBe Right(15)
      Matrix.minor(matrix, MatrixIndex(Row(1), Column(2))) shouldBe Right(-8)
      Matrix.minor(matrix, MatrixIndex(Row(1), Column(3))) shouldBe Right(-25)
      Matrix.minor(matrix, MatrixIndex(Row(2), Column(1))) shouldBe Right(9)
      Matrix.minor(matrix, MatrixIndex(Row(2), Column(2))) shouldBe Right(-14)
      Matrix.minor(matrix, MatrixIndex(Row(2), Column(3))) shouldBe Right(-15)
      Matrix.minor(matrix, MatrixIndex(Row(3), Column(1))) shouldBe Right(-17)
      Matrix.minor(matrix, MatrixIndex(Row(3), Column(2))) shouldBe Right(6)
      Matrix.minor(matrix, MatrixIndex(Row(3), Column(3))) shouldBe Right(13)
    }
  }

  "build" should {
    "fail if elements don't match size" in {
      Matrix.build(MatrixSize(Row(2), Column(2)), Seq(1,2,3)) shouldBe
        Left(ElementSizeError(MatrixSize(Row(2), Column(2)), Seq(1,2,3)))
    }
    "build 2x2 matrix" in {
      Matrix.build(MatrixSize(Row(2), Column(2)), Seq(1,2,3,4)) shouldBe
        Right(Matrix(
          Seq(
            Seq(1,2),
            Seq(3,4)
          )
        ))
    }
    "build 3x2 matrix" in {
      Matrix.build(MatrixSize(Row(3), Column(2)), Seq(1,2,3,4,5,6)) shouldBe
        Right(Matrix(
          Seq(
            Seq(1,2),
            Seq(3,4),
            Seq(5,6)
          )
        ))
    }
  }

  "minorMatrix" should {
    "get minor matrix of 2x2" in {
      Matrix.minorMatrix(Matrix(
        Seq(
          Seq(2, 5),
          Seq(4, 3)
        )
      )) shouldBe Right(Matrix(
        Seq(
          Seq(3,4),
          Seq(5,2)
        )
      ))
    }
    "get minor matrix of 4x4" in {
      Matrix.minorMatrix(Matrix(
        Seq(
          Seq(1, 4,-1,0),
          Seq(2, 3,5,-2),
          Seq(0,3,1,6),
          Seq(3,0,2,1)
        )
      )) shouldBe Right(Matrix(
        Seq(
          Seq(-60, 74,78,-24),
          Seq(-41, -29,75,27),
          Seq(39,17,-29,59),
          Seq(152,44,-24,-26)
        )
      ))
    }
  }

  "cofactorMatrix" should {
    "get minor matrix of 2x2" in {
      Matrix.cofactorMatrix(Matrix(
        Seq(
          Seq(2, 5),
          Seq(4, 3)
        )
      )) shouldBe Right(Matrix(
        Seq(
          Seq(3,-4),
          Seq(-5,2)
        )
      ))
    }
    "get minor matrix of 4x4" in {
      Matrix.cofactorMatrix(Matrix(
        Seq(
          Seq(1, 4,-1,0),
          Seq(2, 3,5,-2),
          Seq(0,3,1,6),
          Seq(3,0,2,1)
        )
      )) shouldBe Right(Matrix(
        Seq(
          Seq(-60, -74,78,24),
          Seq(41, -29,-75,27),
          Seq(39,-17,-29,-59),
          Seq(-152,44,24,-26)
        )
      ))
    }
  }

  "adjoint" should {
    "get adjoint of 2x2 matrix" in {
      Matrix.adjoint(Matrix(
        Seq(
          Seq(5,7),
          Seq(4,3)
        )
      )) shouldBe Right(Matrix(
        Seq(
          Seq(3,-7),
          Seq(-4,5)
        )
      ))
    }
    "get adjoint of 3x3 matrix" in {
      Matrix.adjoint(Matrix(
        Seq(
          Seq(1,-1,0),
          Seq(2,5,-2),
          Seq(3,2,1)
        )
      )) shouldBe Right(Matrix(
        Seq(
          Seq(9,1,2),
          Seq(-8,1,2),
          Seq(-11,-5,7)
        )
      ))
    }
  }

  "concat" should {
    "fail if matrices aren't the same height" in {
      val matrix1 = Matrix.fill(MatrixSize(Row(4), Column(2)), 3)
      val matrix2 = Matrix.fill(MatrixSize(Row(2), Column(2)), 1)
      Matrix.concat(matrix1, matrix2) shouldBe Left(DifferentRowsError(matrix1, matrix2))
    }
    "concat matrices" in {
      Matrix.concat(
        Matrix.fill(MatrixSize(Row(4), Column(2)), 3),
        Matrix.fill(MatrixSize(Row(4), Column(4)), 1)
      ) shouldBe Right(Matrix(
        Seq(
          Seq(3,3,1,1,1,1),
          Seq(3,3,1,1,1,1),
          Seq(3,3,1,1,1,1),
          Seq(3,3,1,1,1,1)
        )
      ))
    }

    "stack" should {
      "fail if matrices aren't the same width" in {
        val matrix1 = Matrix.fill(MatrixSize(Row(4), Column(2)), 3)
        val matrix2 = Matrix.fill(MatrixSize(Row(2), Column(1)), 1)
        Matrix.stack(matrix1, matrix2) shouldBe Left(DifferentColumnsError(matrix1, matrix2))
      }
    }
    "stack matrices" in {
      Matrix.stack(
        Matrix.fill(MatrixSize(Row(2), Column(3)), 3),
        Matrix.fill(MatrixSize(Row(3), Column(3)), 1)
      ) shouldBe Right(Matrix(
        Seq(
          Seq(3,3,3),
          Seq(3,3,3),
          Seq(1,1,1),
          Seq(1,1,1),
          Seq(1,1,1)
        )
      ))
    }
  }

  "addFirstColumn" should {
    "add first column" in {
      Matrix.addFirstColumn(Matrix(Seq(
        Seq(2,3),
        Seq(4,5),
        Seq(6,7),
        Seq(8,9),
      )),1) shouldBe Matrix(Seq(
        Seq(1,2,3),
        Seq(1,4,5),
        Seq(1,6,7),
        Seq(1,8,9),
      ))
    }
  }

}
